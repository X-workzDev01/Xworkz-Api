package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.Team;
import com.xworkz.dream.interfaces.EmailableClient;
import com.xworkz.dream.repository.DreamRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

import freemarker.template.TemplateException;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	@Autowired
	private RegisterRepository repo;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private DreamUtil util;
	@Autowired
	private BirthadayService service;
	@Autowired
	private FollowUpService followUpService;
	@Value("${sheets.rowStartRange}")
	private String rowStartRange;
	@Value("${sheets.rowEndRange}")
	private String rowEndRange;
	@Value("${sheets.traineeSheetName}")
	private String traineeSheetName;
	@Autowired
	private CacheService cacheService;

	private static final Logger logger = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto, HttpServletRequest request)

			throws MessagingException, TemplateException {
		try {

			wrapper.setValuesForTraineeDto(dto);

			List<Object> list = wrapper.extractDtoDetails(dto);

			repo.writeData(spreadsheetId, list);

			if (dto.getBasicInfo().getEmail() != null) {
				logger.info("adding email to the cache", dto.getBasicInfo().getEmail());

				cacheService.addEmailToCache("emailData", spreadsheetId, dto.getBasicInfo().getEmail());

//				//adding email to cache
			}
			if (dto.getBasicInfo().getContactNumber() != null) {
				// adding contactNumber to cache
				logger.info("adding contact number to the cache", dto.getBasicInfo().getContactNumber());
				cacheService.addContactNumberToCache("contactData", spreadsheetId,
						dto.getBasicInfo().getContactNumber());

			}
			// adding to cache
			logger.info("adding register data to the cache:", list);
			cacheService.updateCache("sheetsData", spreadsheetId, list);

			logger.info("adding to follow up:", dto);
			boolean status = followUpService.addToFollowUp(dto, spreadsheetId);

			if (status) {
				logger.info("Data written successfully to spreadsheetId and Added to Follow Up: {}");
				logger.info("saving birthday information", dto);
				service.saveBirthDayInfo(spreadsheetId, dto, request);
				boolean sent = util.sendCourseContent(dto.getBasicInfo().getEmail(),
						dto.getBasicInfo().getTraineeName());

				if (sent) {
					return ResponseEntity.ok("Data written successfully, Added to follow Up, sent course content");
				} else {
					return ResponseEntity.ok("Email not sent, Data written successfully, Added to follow Up");
				}

			}

			return ResponseEntity.ok("Data written successfully, not added to Follow Up");
		} catch (Exception e) {
			logger.error("Error processing request: " + e.getMessage(), e);
			return ResponseEntity.ok("Failed to process the request");
		}
	}

	@Override
	public ResponseEntity<String> emailCheck(String spreadsheetId, String email, HttpServletRequest request) {

		try {
			// if (isCookieValid(request)) {
			List<List<Object>> values = repo.getEmails(spreadsheetId, email);
			for (List<Object> row : values) {
				if (row != null && !row.isEmpty() && row.get(0) != null
						&& row.get(0).toString().equalsIgnoreCase(email)) {
					logger.info("Email exists in spreadsheetId: {}", spreadsheetId);
					return ResponseEntity.status(HttpStatus.CREATED).body("Email exists");

				}
			}
			logger.info("Email does not exist in spreadsheetId: {}", spreadsheetId);
			return ResponseEntity.ok("Email does not exist");

		} catch (Exception e) {
			logger.error("An error occurred while checking email in spreadsheetId: {}", spreadsheetId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	@Override
	public ResponseEntity<String> contactNumberCheck(String spreadsheetId, Long contactNumber,
			HttpServletRequest request) {
		try {
			List<List<Object>> values = repo.getContactNumbers(spreadsheetId);
			for (List<Object> row : values) {
				if (row != null && !row.isEmpty() && row.get(0) != null
						&& row.get(0).toString().equals(String.valueOf(contactNumber))) {
					logger.info("Contact Number exists in spreadsheetId: {}", spreadsheetId);
					return ResponseEntity.status(HttpStatus.CREATED).body("Contact Number exists");
				}

			}
			logger.info("Contact Number does not exist in spreadsheetId: {}", spreadsheetId);
			return ResponseEntity.ok("Contact Number does not exist");
		} catch (Exception e) {
			logger.error("An error occurred while checking Contact Number in spreadsheetId: {}", spreadsheetId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	@Override

	public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows) {
		try {
			List<List<Object>> dataList = repo.readData(spreadsheetId);

			if (dataList != null) {
				// sorting based on registration date
				List<List<Object>> sortedData = dataList.stream().sorted(Comparator.comparing(
						list -> list != null && !list.isEmpty() && list.size() > 24 ? list.get(24).toString() : "",
						Comparator.reverseOrder())).collect(Collectors.toList());

				List<TraineeDto> dtos = getLimitedRows(sortedData, startingIndex, maxRows);

				SheetsDto dto = new SheetsDto(dtos, dtos.size());

				return ResponseEntity.ok(dto);
			}
		} catch (IOException e) {
			logger.error("An error occurred while reading in spreadsheetId: {}", spreadsheetId, e);
		}
		return null;
	}

	@Override

	public List<TraineeDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows) {
		List<TraineeDto> traineeDtos = new ArrayList<>();

		if (values != null) {
			int endIndex = startingIndex + maxRows;

			ListIterator<List<Object>> iterator = values.listIterator(startingIndex);

			while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
				List<Object> row = iterator.next();

				if (row != null && !row.isEmpty()) {
					TraineeDto traineeDto = wrapper.listToDto(row);
					traineeDtos.add(traineeDto);
				}
			}
		}
		return traineeDtos;
	}

	@Override
	public List<TraineeDto> filterData(String spreadsheetId, String searchValue) throws IOException {
		if (searchValue != null && !searchValue.isEmpty()) {
			List<List<Object>> data = repo.readData(spreadsheetId);
			List<List<Object>> filteredLists = data.stream().filter(list -> list.stream().anyMatch(
					value -> value != null && value.toString().toLowerCase().contains(searchValue.toLowerCase())))
					.collect(Collectors.toList());
			List<TraineeDto> flist = new ArrayList<TraineeDto>();
			for (List<Object> list2 : filteredLists) {
				TraineeDto dto = wrapper.listToDto(list2);
				flist.add(dto);
			}
			return flist;
		} else {
			return new ArrayList<>(); // Return an empty list if searchValue is null or empty
		}
	}

	private int findRowIndexByEmail(String spreadsheetId, String email) throws IOException {
		List<List<Object>> data = repo.getEmails(spreadsheetId, email);

		List<List<Object>> values = data;

		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				List<Object> row = values.get(i);
				if (row.size() > 0 && row.get(0).toString().equalsIgnoreCase(email)) {
					return i + 3;
				}
			}
		}
		return -1;
	}

	@Override
	public ResponseEntity<String> update(String spreadsheetId, String email, TraineeDto dto) {
		wrapper.setAdminDto(dto);
		if (email != null && dto.getBasicInfo().getEmail() == "") {

			dto.getBasicInfo().setEmail(email);
		}
		if (email != null && dto.getBasicInfo().getEmail() != null) {

			dto.getBasicInfo().setEmail(dto.getBasicInfo().getEmail());
		}
		wrapper.setFieldValueAsNa(dto);
		try {

			int rowIndex = findRowIndexByEmail(spreadsheetId, email);

			if (rowIndex != -1) {
				String range = traineeSheetName + rowStartRange + rowIndex + ":" + rowEndRange + rowIndex;
				List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(dto));
				if (!values.isEmpty()) {
					List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
					values.set(0, modifiedValues); // Update the values list with the modified sublist

				}
				ValueRange valueRange = new ValueRange();
				valueRange.setValues(values);

				UpdateValuesResponse updated = repo.update(spreadsheetId, range, valueRange);
				if (updated != null && !updated.isEmpty()) {
					followUpService.updateFollowUp(spreadsheetId, email, dto);
					cacheService.getCacheDataByEmail("sheetsData", spreadsheetId, email, dto);
					System.err.println(
							"old email {} new email {}  " + email + "              " + dto.getBasicInfo().getEmail());

					cacheService.getCacheDataByEmail("emailData", spreadsheetId, email, dto.getBasicInfo().getEmail());
					return ResponseEntity.ok("Updated Successfully");
				} else {
					return ResponseEntity.ok("error");
				}

			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
			}
		} catch (IOException | IllegalAccessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");
		}
	}

	@Override
	public ResponseEntity<?> getDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException {
		List<List<Object>> data = repo.readData(spreadsheetId);

		TraineeDto trainee = data.stream().filter(list -> list.contains(email)).findFirst().map(wrapper::listToDto)
				.orElse(null);

		if (trainee != null) {
			return ResponseEntity.ok(trainee);
		} else {
			return new ResponseEntity<>("Email Not Found", HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(String spreadsheetId, String value,
			HttpServletRequest request) {

		List<TraineeDto> suggestion = new ArrayList<>();
		if (value != null) {
			try {
				List<List<Object>> dataList = repo.getEmailsAndNames(spreadsheetId, value);
				List<List<Object>> filteredData = dataList.stream().filter(list -> list.stream().anyMatch(val -> {
					String strVal = val.toString();
					return strVal.toLowerCase().startsWith(value.toLowerCase());
				})).collect(Collectors.toList());

				for (List<Object> list : filteredData) {
					TraineeDto dto = wrapper.listToDto(list);
					suggestion.add(dto);
				}

				return ResponseEntity.ok(suggestion);
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>()); // Return an
																										// empty list on
																										// error
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>()); // Return a bad request on null
																						// value
	}

}
