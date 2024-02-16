package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.CSR;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.service.util.RegistrationUtil;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

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
	@Autowired
	private CsrService csrService;
	@Autowired
	private RegistrationUtil registrationUtil;

	private static final Logger log = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto) {
		try {
			log.info("Writing data for TraineeDto: {}", dto);
			assignCsrDto(dto);
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
			log.info("adding register data to the cache:{}", list);
			cacheService.updateCache("sheetsData", "listOfTraineeData", list);
			log.info("adding to follow up:", dto);
			boolean status = followUpService.addToFollowUp(dto, spreadsheetId);

			if (status) {
				log.info("Data written successfully to spreadsheetId and Added to Follow Up: {}");
				log.info("saving birthday information", dto);
				service.saveBirthDayInfo(spreadsheetId, dto);
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
			log.error("Error processing request:{} ", e.getMessage());
			return ResponseEntity.ok("Failed to process the request");
		}
	}

	private void assignCsrDto(TraineeDto dto) {
		String uniqueId = csrService.generateUniqueID();
		CSR csr = new CSR();
		log.info("set {} if offeredAs a CSR",
				dto.getCourseInfo().getOfferedAs().equalsIgnoreCase(ServiceConstant.CSR.toString()) ? "1" : "0");
		csr.setCsrFlag(dto.getCourseInfo().getOfferedAs().equalsIgnoreCase("CSR Offered") ? "1" : "0");
		csr.setActiveFlag(ServiceConstant.ACTIVE.toString());
		csr.setAlternateContactNumber(0l);
		csr.setUniqueId(dto.getCourseInfo().getOfferedAs().equalsIgnoreCase("CSR Offered") ? uniqueId
				: ServiceConstant.NA.toString());
		csr.setUsnNumber(ServiceConstant.NA.toString());
		dto.setCsrDto(csr);
	}

	@Override
	public ResponseEntity<String> emailCheck(String spreadsheetId, String email) {

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
	public ResponseEntity<String> contactNumberCheck(String spreadsheetId, Long contactNumber) {
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
	public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows, String courseName,
			String collegeName) {
		long endRows = startingIndex + maxRows;
		SheetsDto traineeData = new SheetsDto();
		List<List<Object>> dataList = repo.readData(spreadsheetId);
		if (dataList != null) {
			List<List<Object>> sortedByDate = dataList.stream()
					.sorted(Comparator.comparing(
							list -> list != null && !list.isEmpty() && list.size() > 24 ? list.get(24).toString() : "",
							Comparator.reverseOrder()))
					.collect(Collectors.toList());
			if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
					&& !collegeName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				List<TraineeDto> sortedData = sortedByDate.stream()
						.filter(items -> items != null && items.size() > 9 && items.contains(courseName))
						.filter(items -> items != null && items.size() > 8 && items.contains(collegeName))
						.map(wrapper::listToDto).collect(Collectors.toList());
				traineeData.setSheetsData(
						sortedData.stream().skip(startingIndex).limit(endRows).collect(Collectors.toList()));
				traineeData.setSize(sortedData.size());
				return ResponseEntity.ok(traineeData);
			} else if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				List<List<Object>> sortedCourse = sortedByDate.stream()
						.filter(items -> items != null && items.size() > 9 && items.contains(courseName))
						.collect(Collectors.toList());
				traineeData.setSheetsData(sortedCourse.stream().skip(startingIndex).limit(endRows)
						.map(wrapper::listToDto).collect(Collectors.toList()));
				traineeData.setSize(sortedCourse.size());
				log.info("Returning response for course: {}", courseName);
				return ResponseEntity.ok(traineeData);
			} else if (!collegeName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				List<List<Object>> sortedByCollege = sortedByDate.stream()
						.filter(items -> items != null && items.size() > 8 && items.contains(collegeName))
						.collect(Collectors.toList());
				traineeData.setSheetsData(sortedByCollege.stream().skip(startingIndex).limit(endRows)
						.map(wrapper::listToDto).collect(Collectors.toList()));
				traineeData.setSize(sortedByCollege.size());
				log.info("Returning response for college Name: {}", collegeName);
				return ResponseEntity.ok(traineeData);
			}
			log.info("Returning response for spreadsheetId: {}", spreadsheetId);
			traineeData.setSheetsData(sortedByDate.stream().skip(startingIndex).limit(endRows).map(wrapper::listToDto)
					.collect(Collectors.toList()));
			traineeData.setSize(sortedByDate.size());
			return ResponseEntity.ok(traineeData);
		}
		 return ResponseEntity.ok(traineeData);
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
	public List<TraineeDto> filterData(String spreadsheetId, String searchValue, String courseName,
			String collegeName) {
		List<List<Object>> data = repo.readData(spreadsheetId);
		List<TraineeDto> traineeDtos = new ArrayList<TraineeDto>();
		if (data != null) {
			List<TraineeDto> listOfTrainee = registrationUtil.readOnlyActiveData(data);
			if (searchValue != null && !searchValue.isEmpty()) {
				if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
						&& !collegeName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
					log.info("filterdata by collegeName:{},courseName:{},searchValue:{}", collegeName, courseName,
							searchValue);
					traineeDtos = listOfTrainee.stream()
							.filter(traineeDto -> traineeDto != null
									&& traineeDto.getCourseInfo().getCourse().equalsIgnoreCase(courseName))
							.filter(traineeDto -> traineeDto != null
									&& traineeDto.getEducationInfo().getCollegeName().equalsIgnoreCase(collegeName))
							.filter(traineeDto -> traineeDto != null
									&& traineeDto.getBasicInfo().getEmail().equalsIgnoreCase(searchValue))
							.collect(Collectors.toList());
					return traineeDtos;
				} else if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
					log.info("filterdata by courseName:{},searchValue:{}", courseName, searchValue);
					traineeDtos = listOfTrainee.stream()
							.filter(traineeDto -> traineeDto != null
									&& traineeDto.getCourseInfo().getCourse().equalsIgnoreCase(courseName))
							.filter(traineeDto -> traineeDto != null
									&& traineeDto.getBasicInfo().getEmail().equalsIgnoreCase(searchValue))
							.collect(Collectors.toList());
					return traineeDtos;
				} else if (!collegeName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
					log.info("filterdata by collegeName:{},searchValue:{}", collegeName, searchValue);
					traineeDtos = listOfTrainee.stream()
							.filter(traineeDto -> traineeDto != null
									&& traineeDto.getEducationInfo().getCollegeName().equalsIgnoreCase(collegeName))
							.filter(traineeDto -> traineeDto != null
									&& traineeDto.getBasicInfo().getEmail().equalsIgnoreCase(searchValue))
							.collect(Collectors.toList());
					return traineeDtos;
				} else {
					log.info("filterdata by searchValue:{}", searchValue);
					traineeDtos = listOfTrainee.stream()
							.filter(traineeDto -> traineeDto != null
									&& traineeDto.getBasicInfo().getEmail().equalsIgnoreCase(searchValue)
									|| traineeDto.getBasicInfo().getTraineeName().equalsIgnoreCase(searchValue))
							.collect(Collectors.toList());
					return traineeDtos;
				}
			} else if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
					&& !collegeName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				log.info("filterdata by collegeName:{},courseName:{}", collegeName, courseName);
				traineeDtos = listOfTrainee.stream()
						.filter(traineeDto -> traineeDto != null
								&& traineeDto.getCourseInfo().getCourse().equalsIgnoreCase(courseName))
						.filter(traineeDto -> traineeDto != null
								&& traineeDto.getEducationInfo().getCollegeName().equalsIgnoreCase(collegeName))
						.collect(Collectors.toList());
				return traineeDtos;
			} else if (!collegeName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				log.info("filter by collegeName:{}", collegeName);
				traineeDtos = listOfTrainee.stream()
						.filter(traineeDto -> traineeDto != null
								&& traineeDto.getEducationInfo().getCollegeName().equalsIgnoreCase(collegeName))
						.collect(Collectors.toList());
				return traineeDtos;
			} else if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				log.info("filterdata by courseName:{}", courseName);
				traineeDtos = listOfTrainee.stream()
						.filter(traineeDto -> traineeDto != null
								&& traineeDto.getCourseInfo().getCourse().equalsIgnoreCase(courseName))
						.collect(Collectors.toList());
				return traineeDtos;
			}
		}
		return traineeDtos;
	}

	private int findRowIndexByEmail(String spreadsheetId, String email) {
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
				boolean updateDob = service.updateDob(dto);
				log.info("updated DOB in Sheet,{}", updateDob);
				if (updated != null && !updated.isEmpty()) {
					followUpService.updateFollowUp(spreadsheetId, email, dto);
					cacheService.getCacheDataByEmail("sheetsData", "listOfTraineeData", email, dto);
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
	public TraineeDto getDetailsByEmail(String spreadsheetId, String email) {

		List<List<Object>> data = repo.readData(spreadsheetId);
		TraineeDto trainee = data.stream().filter(list -> list.get(2).toString().contentEquals(email)).findFirst()
				.map(wrapper::listToDto).orElse(null);
		if (trainee != null) {
			return trainee;
		} else {
			return null;

			}
		} catch (IOException e) {
			log.error(" error fetching data", e);
			return null;
		}
	}

	@Override
	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(String spreadsheetId, String value, String courseName,
			String collegeName) {
		List<TraineeDto> suggestion = new ArrayList<>();
		List<List<Object>> dataList = repo.getEmailsAndNames(spreadsheetId, value);
		if (value != null && !value.isEmpty() && dataList != null) {
			List<TraineeDto> listOfTrainee = registrationUtil.convertToTraineeDto(dataList);
			if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
					&& !collegeName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				log.info("suggestion by college:{} and course name:{}", collegeName, courseName);
				suggestion = listOfTrainee.stream()
						.filter(traineeDto -> traineeDto.getCourseInfo().getCourse().equalsIgnoreCase(courseName))
						.filter(traineeDto -> traineeDto.getEducationInfo().getCollegeName()
								.equalsIgnoreCase(collegeName))
						.filter(traineeDto -> traineeDto.getBasicInfo().getTraineeName().toLowerCase()
								.startsWith(value.toLowerCase()))
						.collect(Collectors.toList());
				return ResponseEntity.ok(suggestion);
			} else if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				log.info("suggestion by course name:{}", courseName);
				suggestion = listOfTrainee.stream()
						.filter(traineeDto -> traineeDto.getCourseInfo().getCourse().equalsIgnoreCase(courseName))
						.filter(traineeDto -> traineeDto.getBasicInfo().getTraineeName().toLowerCase()
								.startsWith(value.toLowerCase()))
						.collect(Collectors.toList());
				return ResponseEntity.ok(suggestion);
			} else if (!collegeName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				log.info("suggestion by course name:{}", collegeName);
				suggestion = listOfTrainee.stream().filter(
						traineeDto -> traineeDto.getEducationInfo().getCollegeName().equalsIgnoreCase(collegeName))
						.filter(traineeDto -> traineeDto.getBasicInfo().getTraineeName().toLowerCase()
								.startsWith(value.toLowerCase()))
						.collect(Collectors.toList());
				return ResponseEntity.ok(suggestion);
			} else {
				suggestion = listOfTrainee.stream().filter(traineeDto -> traineeDto.getBasicInfo().getTraineeName()
						.toLowerCase().startsWith(value.toLowerCase())).collect(Collectors.toList());
				log.info("Returning {} search suggestions", suggestion.size());
				return ResponseEntity.ok(suggestion);
			}
		}
		log.warn("Null value provided for search suggestion");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
	}

}
