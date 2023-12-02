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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.TraineeDto;
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

	private static final Logger log = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto, HttpServletRequest request)

			throws MessagingException, TemplateException {
		try {

			wrapper.setValuesForTraineeDto(dto);
			List<Object> list = wrapper.extractDtoDetails(dto);
			repo.writeData(spreadsheetId, list);

			if (dto.getBasicInfo().getEmail() != null) {
				log.info("adding email to the cache", dto.getBasicInfo().getEmail());
				cacheService.addEmailToCache("emailData", spreadsheetId, dto.getBasicInfo().getEmail());

			}
			if (dto.getBasicInfo().getContactNumber() != null) {
				log.info("adding contact number to the cache", dto.getBasicInfo().getContactNumber());
				cacheService.addContactNumberToCache("contactData", spreadsheetId,
						dto.getBasicInfo().getContactNumber());

			}

			log.info("adding register data to the cache:", list);
			cacheService.updateCache("sheetsData", spreadsheetId, list);
			log.info("adding to follow up:", dto);
			boolean status = followUpService.addToFollowUp(dto, spreadsheetId);

			if (status) {
				log.info("Data written successfully to spreadsheetId and Added to Follow Up: {}");
				log.info("saving birthday information", dto);
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
			log.error("Error processing request: " + e.getMessage(), e);
			return ResponseEntity.ok("Failed to process the request");
		}
	}

	@Override
	public ResponseEntity<String> emailCheck(String spreadsheetId, String email, HttpServletRequest request) {

		try {
			List<List<Object>> values = repo.getEmails(spreadsheetId, email);
			for (List<Object> row : values) {
				if (row != null && !row.isEmpty() && row.get(0) != null
						&& row.get(0).toString().equalsIgnoreCase(email)) {
					log.info("Email exists in spreadsheetId: {}", spreadsheetId);
					return ResponseEntity.status(HttpStatus.CREATED).body("Email exists");

				}
			}
			log.info("Email does not exist in spreadsheetId: {}", spreadsheetId);
			return ResponseEntity.ok("Email does not exist");

		} catch (Exception e) {
			log.error("An error occurred while checking email in spreadsheetId: {}", spreadsheetId, e);
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
					log.info("Contact Number exists in spreadsheetId: {}", spreadsheetId);
					return ResponseEntity.status(HttpStatus.CREATED).body("Contact Number exists");
				}
			}
			log.info("Contact Number does not exist in spreadsheetId: {}", spreadsheetId);
			return ResponseEntity.ok("Contact Number does not exist");
		} catch (Exception e) {
			log.error("An error occurred while checking Contact Number in spreadsheetId: {}", spreadsheetId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	@Override
	public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows, String courseName) {
		try {
			List<List<Object>> dataList = repo.readData(spreadsheetId);
			if (dataList != null) {
				List<List<Object>> sortedData = dataList.stream().sorted(Comparator.comparing(
						list -> list != null && !list.isEmpty() && list.size() > 24 ? list.get(24).toString() : "",
						Comparator.reverseOrder())).collect(Collectors.toList());
				if (!courseName.equals("null")) {
					List<List<Object>> sortedCourse = sortedData.stream()
							.filter(items -> items != null && items.size() > 9 && items.contains(courseName))
							.collect(Collectors.toList());
					List<TraineeDto> dtos = getLimitedRows(sortedCourse, startingIndex, maxRows);
					SheetsDto dto = new SheetsDto(dtos, sortedCourse.size());
					log.info("Returning response for course: {}", courseName);
					return ResponseEntity.ok(dto);
				}

				List<TraineeDto> dtos = getLimitedRows(sortedData, startingIndex, maxRows);

				SheetsDto dto = new SheetsDto(dtos, sortedData.size());
				log.info("Returning response for spreadsheetId: {}", spreadsheetId);
				return ResponseEntity.ok(dto);
			}

		} catch (IOException e) {
			log.error("An error occurred while reading in spreadsheetId: {}", spreadsheetId, e);
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
			log.info("Returning {} TraineeDto objects", traineeDtos.size());
		}
		return traineeDtos;
	}

	@Override
	public List<TraineeDto> filterData(String spreadsheetId, String searchValue, String courseName) throws IOException {
		try {
			if (searchValue != null && !searchValue.isEmpty()) {
				log.info("Filtering data in spreadsheetId: {} with search value: {}", spreadsheetId, searchValue);
				List<List<Object>> data = repo.readData(spreadsheetId);
				List<List<Object>> filteredLists = data.stream().filter(list -> list.stream().anyMatch(
						value -> value != null && value.toString().toLowerCase().contains(searchValue.toLowerCase())))
						.collect(Collectors.toList());
				if (!courseName.equals("null")) {
					List<TraineeDto> flist = filteredLists.stream().map(items -> wrapper.listToDto(items))
							.filter(dto -> dto.getCourseInfo().getCourse().equalsIgnoreCase(courseName))
							.collect(Collectors.toList());
					log.info("Filtered {} TraineeDto objects", flist.size());

					return flist;

				} else {
					List<TraineeDto> flist = filteredLists.stream().map(items -> wrapper.listToDto(items))
							.filter(dto -> dto.getBasicInfo().getEmail().equalsIgnoreCase(searchValue))
							.collect(Collectors.toList());
					log.info("Filtered {} TraineeDto objects", flist.size());

					return flist;

				}
			} else {
				log.warn("Search value is null or empty. Returning an empty list.");
				return new ArrayList<>();
			}
		} catch (IOException e) {
			log.error("An error occurred while filtering data in spreadsheetId: {}", spreadsheetId, e);
			throw e;
		}
	}

	private int findRowIndexByEmail(String spreadsheetId, String email) throws IOException {
		try {
			log.info("Finding row index by email in spreadsheetId: {} for email: {}", spreadsheetId, email);
			List<List<Object>> data = repo.getEmails(spreadsheetId, email);
			List<List<Object>> values = data;
			if (values != null) {
				for (int i = 0; i < values.size(); i++) {
					List<Object> row = values.get(i);
					if (row.size() > 0 && row.get(0).toString().equalsIgnoreCase(email)) {
						log.info("Found row index {} for email: {}", i + 3, email);
						return i + 3;
					}
				}
			}
			log.info("Email {} not found in the spreadsheet.", email);
			return -1;
		} catch (IOException e) {
			log.error("An error occurred while finding row index by email in spreadsheetId: {}", spreadsheetId, e);
			throw e;
		}
	}

	@Override
	public ResponseEntity<String> update(String spreadsheetId, String email, TraineeDto dto) {
		log.info("Updating data in spreadsheetId: {} for email: {}", spreadsheetId, email);
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
				log.info("Found row index {} for email: {}", rowIndex, email);
				String range = traineeSheetName + rowStartRange + rowIndex + ":" + rowEndRange + rowIndex;
				List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(dto));
				if (!values.isEmpty()) {
					List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
					values.set(0, modifiedValues);
				}
				ValueRange valueRange = new ValueRange();
				valueRange.setValues(values);

				UpdateValuesResponse updated = repo.update(spreadsheetId, range, valueRange);
				if (updated != null && !updated.isEmpty()) {
					followUpService.updateFollowUp(spreadsheetId, email, dto);
					cacheService.getCacheDataByEmail("sheetsData", spreadsheetId, email, dto);
					log.info("Updated Successfully. Email: {}", email);
					cacheService.getCacheDataByEmail("emailData", spreadsheetId, email, dto.getBasicInfo().getEmail());
					return ResponseEntity.ok("Updated Successfully");
				} else {
					log.error("Error updating data. Email: {}", email);
					return ResponseEntity.ok("error");
				}

			} else {
				log.warn("Email not found: {}", email);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
			}
		} catch (IOException | IllegalAccessException e) {
			log.error("An error occurred while updating data. Email: {}", email, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");
		}
	}

	@Override
	public TraineeDto getDetailsByEmail(String spreadsheetId, String email)
			throws IOException {
		List<List<Object>> data = repo.readData(spreadsheetId);
		TraineeDto trainee = data.stream().filter(list -> list.contains(email)).findFirst().map(wrapper::listToDto)
				.orElse(null);

		if (trainee != null) {
			return trainee;
		} else {
			return null;

		}
	}

	@Override
	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(String spreadsheetId, String value, String courseName) {
		List<TraineeDto> suggestion = new ArrayList<>();
		if (value != null) {
			try {
				System.err.println(courseName);
				if (!courseName.equalsIgnoreCase("null")) {
					List<List<Object>> dataList = repo.getEmailsAndNames(spreadsheetId, value).stream()
							.filter(list -> list.get(9) != null && list.get(9).toString().equalsIgnoreCase(courseName))
							.collect(Collectors.toList());
					List<List<Object>> filteredData = dataList.stream().filter(list -> list.stream().anyMatch(val -> {
						return val.toString().toLowerCase().startsWith(value.toLowerCase());
					})).collect(Collectors.toList());
					for (List<Object> list : filteredData) {
						TraineeDto dto = wrapper.listToDto(list);
						suggestion.add(dto);
					}
				} else {
					List<List<Object>> dataList = repo.getEmailsAndNames(spreadsheetId, value);
					List<List<Object>> filteredData = dataList.stream().filter(list -> list.stream().anyMatch(val -> {
						return val.toString().toLowerCase().startsWith(value.toLowerCase());
					})).collect(Collectors.toList());
					for (List<Object> list : filteredData) {
						TraineeDto dto = wrapper.listToDto(list);
						suggestion.add(dto);
					}
				}

				log.info("Returning {} search suggestions", suggestion.size());
				return ResponseEntity.ok(suggestion);
			} catch (IOException e) {
				log.error("An error occurred while getting search suggestion in spreadsheetId: {}", spreadsheetId, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
			}
		}
		log.warn("Null value provided for search suggestion");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
	}

}
