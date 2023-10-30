package com.xworkz.dream.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AdminDto;
import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.CourseDto;
import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.OthersDto;
import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.Team;
import com.xworkz.dream.interfaces.EmailableClient;
import com.xworkz.dream.repository.DreamRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

import freemarker.template.TemplateException;

@Service
public class DreamServiceImpl implements DreamService {

	@Autowired
	private DreamRepository repo;
	@Autowired
	private DreamWrapper wrapper;
	private BatchDetails batch;
	@Autowired
	private DreamUtil util;
	@Value("${login.sheetId}")
	private String id;
	private HttpServletRequest request;
	private ResponseEntity<SheetNotificationDto> response;
	private String loginEmail;
	private List<Team> users = new ArrayList<Team>();
	@Autowired
	private ResourceLoader resourceLoader;
	@Value("${login.teamFile}")
	private String userFile;
	@Autowired
	private EmailableClient emailableClient;
	@Value("${sheets.rowStartRange}")
	private String rowStartRange;
	@Value("${sheets.rowEndRange}")
	private String rowEndRange;
	@Value("${sheets.followUpRowCurrentStartRange}")
	private String followUpRowCurrentStartRange;
	@Value("${sheets.followUpRowCurrentEndRange}")
	private String followUpRowCurrentEndRange;
	@Value("${sheets.traineeSheetName}")
	private String traineeSheetName;
	@Value("${sheets.followUpSheetName}")
	private String followUpSheetName;
	@Value("${sheets.followUprowStartRange}")
	private String followUprowStartRange;
	@Value("${sheets.followUprowEndRange}")
	private String followUprowEndRange;
	@Value("${sheets.liveKey}")
	private String API_KEY;

	@Autowired
	private CacheService cacheService;

	private static final Logger logger = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public synchronized ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto,
			HttpServletRequest request)

			throws MessagingException, TemplateException {
		try {

			List<List<Object>> data = repo.getIds(spreadsheetId).getValues();
			int size = data != null ? data.size() : 0;
			dto.setId(size += 1);
			wrapper.setValuesForTraineeDto(dto);

			List<Object> list = wrapper.extractDtoDetails(dto);

			repo.writeData(spreadsheetId, list);
			// adding to cache
			cacheService.updateCache("sheetsData", spreadsheetId, list);

			boolean status = addToFollowUp(dto, spreadsheetId);

			if (status) {
				logger.info("Data written successfully to spreadsheetId and Added to Follow Up: {}", spreadsheetId);

				saveBirthDayInfo(spreadsheetId, dto, request);
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

	public ResponseEntity<String> writeDataEnquiry(String spreadsheetId, TraineeDto dto, HttpServletRequest request)

			throws MessagingException, TemplateException {
		try {
			List<List<Object>> data = repo.getIds(spreadsheetId).getValues();
			int size = data != null ? data.size() : 0;
			dto.setId(size += 1);
			wrapper.setValuesForTraineeDto(dto);

			List<Object> list = wrapper.extractDtoDetails(dto);

			repo.writeData(spreadsheetId, list);
			// adding data to the cache
			cacheService.updateCache("sheetsData", spreadsheetId, list);
			saveBirthDayInfo(spreadsheetId, dto, request);

			boolean status = addToFollowUpEnquiry(dto, spreadsheetId);

			if (status) {
				logger.info("Data written successfully to spreadsheetId and Added to Follow Up: {}", spreadsheetId);
				util.sms(dto);

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
	public boolean addToFollowUp(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException {
		if (traineeDto == null) {
			return false;
		}

		FollowUpDto followUpDto = wrapper.setFollowUp(traineeDto);
		if (followUpDto == null) {
			return false;
		}
		if (followUpDto.getCallback() == null) {
			followUpDto.setCallback(LocalDate.now().plusDays(1).toString());
		}
		List<Object> data = wrapper.extractDtoDetails(followUpDto);
		if (data == null) {
			return false;
		}
		repo.saveToFollowUp(spreadSheetId, data);
		return true;
	}

	private boolean addToFollowUpEnquiry(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException {
		if (traineeDto == null) {
			return false;
		}

		FollowUpDto followUpDto = wrapper.setFollowUpEnwuiry(traineeDto);
		if (followUpDto == null) {
			return false;
		}
		if (followUpDto.getCallback() == null) {
			followUpDto.setCallback(LocalDate.now().plusDays(1).toString());
		}
		List<Object> data = wrapper.extractDtoDetails(followUpDto);
		if (data == null) {
			return false;
		}
		boolean save = repo.saveToFollowUp(spreadSheetId, data);
		return save;

	}

	@Override
	public ResponseEntity<String> emailCheck(String spreadsheetId, String email, HttpServletRequest request) {

		try {
			// if (isCookieValid(request)) {
			ValueRange values = repo.getEmails(spreadsheetId);
			if (values != null && values.getValues() != null) {
				for (List<Object> row : values.getValues()) {
					if (row != null && !row.isEmpty() && row.get(0) != null
							&& row.get(0).toString().equalsIgnoreCase(email)) {
						logger.info("Email exists in spreadsheetId: {}", spreadsheetId);
						return ResponseEntity.status(HttpStatus.CREATED).body("Email exists");
					}
				}
			}
			logger.info("Email does not exist in spreadsheetId: {}", spreadsheetId);
			return ResponseEntity.ok("Email does not exist");
//			} else {
//				// Invalid cookie
//				logger.info("Invalid cookie in the request");
//				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid cookie");
//			}
		} catch (Exception e) {
			logger.error("An error occurred while checking email in spreadsheetId: {}", spreadsheetId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	@Override
	public ResponseEntity<String> contactNumberCheck(String spreadsheetId, Long contactNumber,
			HttpServletRequest request) {
		try {
			ValueRange values = repo.getContactNumbers(spreadsheetId);
			if (values != null && values.getValues() != null) {
				for (List<Object> row : values.getValues()) {
					if (row != null && !row.isEmpty() && row.get(0) != null
							&& row.get(0).toString().equals(String.valueOf(contactNumber))) {
						logger.info("Contact Number exists in spreadsheetId: {}", spreadsheetId);
						return ResponseEntity.status(HttpStatus.CREATED).body("Contact Number exists");
					}
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
	// @Cacheable(value = "sheetsData", key = "#spreadsheetId", unless = "#result ==
	// null")
	public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows) {
		try {
			List<List<Object>> dataList = repo.readData(spreadsheetId);

			if (dataList != null) {
				// sorting based on registration date
				List<List<Object>> sortedData = dataList.stream().sorted(Comparator.comparing(
						list -> list != null && !list.isEmpty() && list.size() > 24 ? list.get(24).toString() : "",
						Comparator.reverseOrder())).collect(Collectors.toList());

				List<TraineeDto> dtos = getLimitedRows(sortedData, startingIndex, maxRows);

				SheetsDto dto = new SheetsDto(dtos, dataList.size());

				return ResponseEntity.ok(dto);
			}
		} catch (IOException e) {
			logger.error("An error occurred while reading in spreadsheetId: {}", spreadsheetId, e);
		}
		return null;
	}

	@Override
	// @Cacheable(value = "sheetsData", key = "#spreadsheetId", unless = "#result ==
	// null")
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

				// removing id while update
				if (!values.isEmpty()) {
					List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
					values.set(0, modifiedValues); // Update the values list with the modified sublist

				}
				ValueRange valueRange = new ValueRange();
				valueRange.setValues(values);

				UpdateValuesResponse updated = repo.update(spreadsheetId, range, valueRange);
				if (updated != null && !updated.isEmpty()) {
					updateFollowUp(spreadsheetId, email, dto);
					cacheService.getCacheDataByEmail("sheetsData", spreadsheetId, email, dto);
					// repo.evictAllCachesOnTraineeDetails();
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
	public boolean updateFollowUp(String spreadsheetId, String email, TraineeDto dto)
			throws IOException, IllegalAccessException {

		FollowUpDto followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);
		if (followUpDto == null) {
			return false;
		}

		int rowIndex = findByEmailForUpdate(spreadsheetId, email);
		if (rowIndex != -1) {
			String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;

			// Initialize followUpDto with the existing data
			followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);
			// Update the email from the TraineeDto
			followUpDto.getBasicInfo().setEmail(dto.getBasicInfo().getEmail());
			followUpDto.getBasicInfo().setContactNumber(dto.getBasicInfo().getContactNumber());
			followUpDto.setAdminDto(dto.getAdminDto());
			List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followUpDto));
			ValueRange valueRange = new ValueRange();

//			//removing id while update
			if (!values.isEmpty()) {
				List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
				values.set(0, modifiedValues); // Update the values list with the modified sublist

			}
			valueRange.setValues(values);

			UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);

			if (updated != null && !updated.isEmpty()) {
				// repo.evictAllCachesOnTraineeDetails();
				cacheService.updateCacheFollowUp("followUpDetails", spreadsheetId, email, followUpDto);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private int findRowIndexByEmail(String spreadsheetId, String email) throws IOException {
		ValueRange data = repo.getEmails(spreadsheetId);
		List<List<Object>> values = data.getValues();
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
	public boolean updateCurrentFollowUp(String calBack, String spreadsheetId, String email, String currentStatus,
			String currentlyFollowedBy, String joiningDate) throws IOException, IllegalAccessException {
		FollowUpDto followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);
		System.out.println("Email : "+email);
		int rowIndex = findByEmailForUpdate(spreadsheetId, email);
		if (rowIndex != -1) {
			String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;
			UpdateValuesResponse updated = setFollowUpDto(calBack, spreadsheetId, currentStatus, currentlyFollowedBy,
					followUpDto, joiningDate, range);
			if (updated != null && !updated.isEmpty()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private UpdateValuesResponse setFollowUpDto(String callBack, String spreadsheetId, String currentStatus,
			String currentlyFollowedBy, FollowUpDto followUpDto, String joiningDate, String range)
			throws IllegalAccessException, IOException {
		

		AdminDto existingAdminDto = followUpDto.getAdminDto();
		AdminDto adminDto = new AdminDto();

		if (existingAdminDto != null) {
			adminDto.setCreatedBy(existingAdminDto.getCreatedBy());
			adminDto.setCreatedOn(existingAdminDto.getCreatedOn());
		}
		if (currentStatus != null && !currentStatus.equals("NA")) {
			followUpDto.setCurrentStatus(currentStatus);
		}
		if (joiningDate != null && !joiningDate.equals("NA")) {
			followUpDto.setJoiningDate(joiningDate);
		}

		adminDto.setUpdatedBy(currentlyFollowedBy);
		adminDto.setUpdatedOn(LocalDateTime.now().toString());
		if (callBack != null && !callBack.equals("NA")) {
			followUpDto.setCallback(LocalDateTime.of(LocalDate.parse(callBack), LocalTime.now()).toString());
		}
		if (callBack != null && callBack.equals("NA")) {
			followUpDto.setCallback(LocalDateTime.of(LocalDate.now(), LocalTime.now()).plusDays(1).toString());
		}
		followUpDto.setAdminDto(adminDto);

		System.out.println("followUpDto data:"+followUpDto);
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followUpDto));
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);
		return updated;
	}

	@Override
	public ResponseEntity<String> updateFollowUpStatus(String spreadsheetId, StatusDto statusDto
			) {
		try {
			List<List<Object>> data = repo.getStatusId(spreadsheetId).getValues();
			StatusDto sdto = wrapper.setFollowUpStatus(statusDto, data);
			System.out.println("status data:"+sdto);

			List<Object> statusData = wrapper.extractDtoDetails(sdto);
			boolean status = repo.updateFollowUpStatus(spreadsheetId, statusData);
//			cacheService.updateFollowUpStatusInCache("followUpStatusDetails", spreadsheetId, statusData);

			if (status == true) {
				updateCurrentFollowUp(statusDto.getCallBack(), spreadsheetId, statusDto.getBasicInfo().getEmail(),
						statusDto.getAttemptStatus(), statusDto.getAttemptedBy(), statusDto.getJoiningDate());
				// repo.evictAllCachesOnTraineeDetails();
//				cacheService.updateFollowUpStatus("followUpDetails", spreadsheetId, statusDto);
			}
			return ResponseEntity.ok("Follow Status Updated for ID :  " + statusDto.getId());
		} catch (IOException | IllegalAccessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred with credentials file ");
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

	@Override
	public ResponseEntity<?> getDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException {
		List<List<Object>> data = repo.readData(spreadsheetId);

		TraineeDto trainee = data.stream().filter(list -> list.contains(email)).findFirst().map(wrapper::listToDto)
				.orElse(null);

		System.out.println("trainee data:" + trainee);

		if (trainee != null) {
			return ResponseEntity.ok(trainee);
		} else {
			return new ResponseEntity<>("Email Not Found", HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<FollowUpDto> getFollowUpByEmail(String spreadsheetId, String email,
			HttpServletRequest request) throws IOException {
		List<List<Object>> data = repo.getFollowUpDetails(spreadsheetId);
		FollowUpDto followUp = data.stream().filter(list -> list.size() > 2 && list.get(2) instanceof String)
				.filter(list -> ((String) list.get(2)).equalsIgnoreCase(email)).findFirst()
				.map(wrapper::listToFollowUpDTO).orElse(null);

		if (followUp != null) {
			return ResponseEntity.ok(followUp);
		} else {
			return ResponseEntity.ok(followUp);
		}
	}

	@Override
	public ResponseEntity<FollowUpDataDto> getFollowUpDetails(String spreadsheetId, int startingIndex, int maxRows,
			String status) throws IOException {
		List<FollowUpDto> followUpDto = new ArrayList<FollowUpDto>();
		List<List<Object>> lists = repo.getFollowUpDetails(spreadsheetId);
		List<List<Object>> traineeData = repo.readData(spreadsheetId);

		if (status != null && !status.isEmpty() && lists != null) {
			List<List<Object>> data = lists.stream().filter(
					list -> list.stream().anyMatch(value -> value != null && value.toString().equalsIgnoreCase(status)))
					.collect(Collectors.toList());

			if (data != null) {
				List<List<Object>> sortedData = data.stream().sorted(Comparator.comparing(
						list -> list != null && !list.isEmpty() && list.size() > 4 ? list.get(4).toString() : "",
						Comparator.reverseOrder())).collect(Collectors.toList());

				followUpDto = getFollowUpRows(sortedData, startingIndex, maxRows);
				followUpDto.stream().forEach(dto -> {
					TraineeDto traineedto = getTraineeDtoByEmail(traineeData, dto.getBasicInfo().getEmail());
					if (traineedto != null) {
						dto.setCourseName(traineedto.getCourseInfo().getCourse());
					}
				});

				FollowUpDataDto followUpDataDto = new FollowUpDataDto(followUpDto, data.size());
				// repo.evictFollowUpStatusDetails();
				return ResponseEntity.ok(followUpDataDto);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return a not found response if data is
																				// null
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return a bad request if status is null
																				// or empty
		}
	}

	private TraineeDto getTraineeDtoByEmail(List<List<Object>> traineeData, String email) {
		if (traineeData == null || email == null) {
			return null;
		}
		return traineeData.stream()
				.filter(row -> row.size() > 2 && row.get(2) != null && row.get(2).toString().equalsIgnoreCase(email))
				.map(wrapper::listToDto).findFirst().orElse(null);
	}

	@Override
	public List<FollowUpDto> getFollowUpRows(List<List<Object>> values, int startingIndex, int maxRows) {
		List<FollowUpDto> followUpDtos = new ArrayList<>();

		int endIndex = startingIndex + maxRows;

		ListIterator<List<Object>> iterator = values.listIterator(startingIndex);
		while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
			List<Object> row = iterator.next();

			if (row != null && !row.isEmpty()) {
				FollowUpDto followupDto = wrapper.listToFollowUpDTO(row);

				followUpDtos.add(followupDto);
			}
		}
		return followUpDtos;
	}

	@Override
	public List<StatusDto> getStatusDetails(String spreadsheetId, int startingIndex, int maxRows, String email,
			HttpServletRequest request) throws IOException {
		List<StatusDto> statusDto = new ArrayList<>();
		List<List<Object>> dataList = repo.getFollowUpStatusDetails(spreadsheetId);
		List<List<Object>> data = dataList.stream()
				.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(email)))
				.collect(Collectors.toList());
		statusDto = getFollowUpStatusData(data, startingIndex, maxRows);
		return statusDto;
	}

	@Override
	public List<StatusDto> getStatusDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException {
		List<StatusDto> statusDto = new ArrayList<>();
		List<List<Object>> dataList = repo.getFollowUpStatusDetails(spreadsheetId);
		System.out.println(dataList);
		if (email != null && dataList != null && !dataList.isEmpty()) {
			List<List<Object>> data = dataList.stream()
					.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(email)))
					.collect(Collectors.toList());
			Collections.reverse(data);
			for (List<Object> row : data) {
				StatusDto dto = wrapper.listToStatusDto(row);
				statusDto.add(dto);
			}
		}
		// repo.evictFollowUpStatusDetails();
		return statusDto;
	}

	@Override
	public List<StatusDto> getFollowUpStatusData(List<List<Object>> values, int startingIndex, int maxRows) {
		List<StatusDto> statusDtos = new ArrayList<>();

		int endIndex = startingIndex + maxRows;
		ListIterator<List<Object>> iterator = values.listIterator(startingIndex);

		while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
			List<Object> row = iterator.next();

			if (row != null && !row.isEmpty()) {
				StatusDto statusDto = wrapper.listToStatusDto(row);
				statusDtos.add(statusDto);
			}
		}

		return statusDtos;
	}

	@Override
	public ResponseEntity<String> saveDetails(String spreadsheetId, BatchDetailsDto dto, HttpServletRequest request)
			throws IOException, IllegalAccessException {
		List<List<Object>> data = repo.getBatchId(spreadsheetId).getValues();
		int size = data != null ? data.size() : 0;
		dto.setId(size += 1);
		List<Object> list = wrapper.extractDtoDetails(dto);
		boolean save = repo.saveBatchDetails(spreadsheetId, list);
		if (save == true) {
			return ResponseEntity.ok("Batch details added successfully");
		} else {
			return ResponseEntity.ok("Batch details Not added");
		}
	}

	@Override
	public ResponseEntity<String> saveBirthDayInfo(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws IllegalAccessException, IOException {
		BirthDayInfoDto birthday = new BirthDayInfoDto();
		List<List<Object>> data = repo.getBirthDayId(spreadsheetId).getValues();
		int size = data != null ? data.size() : 0;
		birthday.setDto(dto.getBasicInfo());
		birthday.setId(size += 1);
		List<Object> list = wrapper.extractDtoDetails(birthday);

		boolean save = repo.saveBirthDayDetails(spreadsheetId, list);
		if (save != false) {
			return ResponseEntity.ok("Birth day information added successfully");
		}
		return ResponseEntity.ok("Birth day information Not added");
	}

	@Override
	public ResponseEntity<List<Object>> getCourseNameByStatus(String spreadsheetId, String status) {
		List<List<Object>> courseNameByStatus;
		try {
			courseNameByStatus = repo.getCourseDetails(spreadsheetId);
			List<Object> coursename = new ArrayList<Object>();
			if (courseNameByStatus != null) {
				for (List<Object> row : courseNameByStatus) {
					if (((String) row.get(7)).equalsIgnoreCase(status)) {
						coursename.add(row.get(1));

					}
				}
			}
			return ResponseEntity.ok(coursename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public ResponseEntity<BatchDetails> getBatchDetailsByCourseName(String spreadsheetId, String courseName)
			throws IOException {
		List<List<Object>> detailsByCourseName = repo.getCourseDetails(spreadsheetId);
		batch = null;

		List<List<Object>> filter = detailsByCourseName.stream()
				.filter(e -> e.contains(courseName) && e.contains("Active")).collect(Collectors.toList());
		filter.stream().forEach(item -> {
			this.batch = wrapper.batchDetailsToDto(item);
		});
		if (batch != null) {
			return ResponseEntity.ok(this.batch);
		}
		return null;
	}

	@Override
	public FollowUpDto getFollowUpDetailsByEmail(String spreadsheetId, String email) throws IOException {

		FollowUpDto followUpDto = new FollowUpDto();
		if (email != null && !email.isEmpty()) {
			List<List<Object>> lists = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> data = lists.stream()
					.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(email)))
					.collect(Collectors.toList());
			for (List<Object> list : data) {
				followUpDto = wrapper.listToFollowUpDTO(list);
			}
			return followUpDto;
		}
		return null;
	}

	private int findByEmailForUpdate(String spreadsheetId, String email) throws IOException {

		ValueRange data = repo.getEmailList(spreadsheetId);
		List<List<Object>> values = data.getValues();
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				List<Object> row = values.get(i);
				if (row.size() > 0 && row.get(0).toString().equalsIgnoreCase(email)) {
					return i + 2;
				}
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private List<Team> getTeam() throws IOException {

		Yaml yaml = new Yaml();
		Resource resource = resourceLoader.getResource(userFile);
		File file = resource.getFile();
		FileInputStream inputStream = new FileInputStream(file);
		Map<String, Map<Object, Object>> yamlData = (Map<String, Map<Object, Object>>) yaml.load(inputStream);
		List<Object> list = (List<Object>) yamlData.get("team");
		ObjectMapper objectMapper = new ObjectMapper();

		for (Object object : list) {
			Team user = objectMapper.convertValue(object, Team.class);
			users.add(user);
		}
		return users;
	}

	@Override
	@Scheduled(fixedRate = 30 * 60 * 1000) // 1000 milliseconds = 1 seconds
	public void notification() {
		try {
			List<Team> teamList = getTeam();
			ResponseEntity<SheetNotificationDto> notificationDto = notification(id, loginEmail, teamList, request);

		} catch (IOException e) {
			throw new RuntimeException("Exception occurred: " + e.getMessage(), e);
		}

	}

	@Override
	public ResponseEntity<SheetNotificationDto> notification(String spreadsheetId, String email, List<Team> teamList,
			HttpServletRequest requests) throws IOException {
		List<String> statusCheck = Stream.of(Status.Busy.toString(), Status.New.toString(),
				Status.Interested.toString(), Status.RNR.toString(), Status.Not_interested.toString().replace('_', ' '),
				Status.Incomingcall_not_available.toString().replace('_', ' '),
				Status.Not_reachable.toString().replace('_', ' '), Status.Let_us_know.toString().replace('_', ' '),
				Status.Need_online.toString().replace('_', ' ')).collect(Collectors.toList());

		LocalTime time = LocalTime.of(17, 59, 01, 500_000_000);
		List<StatusDto> notificationStatus = new ArrayList<StatusDto>();
		List<StatusDto> today = new ArrayList<StatusDto>();
		List<StatusDto> yesterday = new ArrayList<StatusDto>();
		List<StatusDto> afterFoureDay = new ArrayList<StatusDto>();
		if (spreadsheetId != null) {
			List<List<Object>> listOfData = repo.notification(spreadsheetId);
			if (listOfData != null) {
				List<List<Object>> list = listOfData.stream().filter(items -> !items.contains("NA"))
						.collect(Collectors.toList());

				if (!listOfData.isEmpty()) {
					if (email != null) {
						listOfData.stream().forEach(e -> {
							StatusDto dto = wrapper.listToStatusDto(e);

							if (LocalDate.now().isEqual(LocalDate.parse(dto.getCallBack()))
									&& email.equalsIgnoreCase(dto.getAttemptedBy())
									&& statusCheck.contains(dto.getAttemptStatus())) {
								today.add(dto);
							}
							if (LocalDate.now().minusDays(1).isEqual(LocalDate.parse(dto.getCallBack()))
									&& email.equalsIgnoreCase(dto.getAttemptedBy())
									&& statusCheck.contains(dto.getAttemptStatus())) {
								yesterday.add(dto);

							}
							if (LocalDate.now().plusDays(4).isEqual(LocalDate.parse(dto.getCallBack()))
									&& email.equalsIgnoreCase(dto.getAttemptedBy())
									&& statusCheck.contains(dto.getAttemptStatus())) {
								afterFoureDay.add(dto);

							}

						});
						SheetNotificationDto dto = new SheetNotificationDto(yesterday, today, afterFoureDay);
						response = ResponseEntity.ok(dto);

					}

					listOfData.stream().forEach(e -> {
						StatusDto dto = wrapper.listToStatusDto(e);
						if (dto.getCallBack() != null && dto.getCallBack().toString() != "NA") {
							if (LocalDateTime.now()
									.isAfter(LocalDateTime.of((LocalDate.parse(dto.getCallBack())), time))
									&& LocalDateTime.now().isBefore(LocalDateTime
											.of((LocalDate.parse(dto.getCallBack())), time.plusMinutes(26)))) {

								if (statusCheck.contains(dto.getAttemptStatus())
										&& LocalDate.now().isEqual(LocalDate.parse(dto.getCallBack()))) {

									notificationStatus.add(dto);

								}

							}
						}

					});
				}
			}
			if (LocalTime.now().isAfter(time) && LocalTime.now().isBefore(time.plusMinutes(26))) {

				if (!notificationStatus.isEmpty()) {

					util.sendNotificationToEmail(teamList, notificationStatus);

				}

			}

		}
		return null;

	}

	@Override
	public ResponseEntity<SheetNotificationDto> setNotification(@Value("${myapp.scheduled.param}") String email,
			@Value("${myapp.scheduled.param}") HttpServletRequest requests) throws IOException {
		this.request = requests;
		this.loginEmail = email;
		notification();
		return response;

	}

	@Override
	public String verifyEmails(String email) {
		return emailableClient.verifyEmail(email, API_KEY);
	}

	@Override
	public boolean addEnquiry(EnquiryDto enquiryDto, String spreadsheetId, HttpServletRequest request) {
		TraineeDto traineeDto = new TraineeDto();
		EnquiryDto validatedEnquiryDto = wrapper.validateEnquiry(enquiryDto);

		traineeDto.setCourseInfo(new CourseDto("NA"));
		traineeDto.setOthersDto(new OthersDto("NA"));
		traineeDto.setAdminDto(enquiryDto.getAdminDto());
		traineeDto.setBasicInfo(enquiryDto.getBasicInfo());
		traineeDto.setEducationInfo(enquiryDto.getEducationInfo());

		try {
			writeDataEnquiry(spreadsheetId, traineeDto, request);
		} catch (MessagingException | TemplateException e) {
			// TODO Auto-generated catch block
			logger.error("Error Writing enqiry data to sheet");
		}
		return true;

	}

	@Override
	public boolean updateCurrentFollowUp(String spreadsheetId, String email, String currentStatus,
			String currentlyFollowedBy) throws IOException, IllegalAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResponseEntity<String> updateFollowUp(String spreadsheetId, String email, FollowUpDto followDto)
			throws IOException, IllegalAccessException {

		int rowIndex = findByEmailForUpdate(spreadsheetId, email);

		String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followDto));
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);
		if (updated.isEmpty()) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");
		} else {
			// repo.evictAllCachesOnTraineeDetails();
			return ResponseEntity.ok("Updated Successfully");
		}
	}

	@Override
	public ResponseEntity<FollowUpDataDto> getFollowStatusByDate(String date, int startIndex, int endIndex,
			String spreadsheetID, HttpServletRequest request) throws IOException {
		List<List<Object>> dataList = repo.getFollowupStatusByDate(spreadsheetID);

		if (dataList != null && date != null) {
			List<List<Object>> list = dataList.stream().filter(item -> item.get(9).equals(date))
					.collect(Collectors.toList());

			List<FollowUpDto> dto = getLimitedRowsBatchAndDate(list, date, startIndex, endIndex);
			Collections.reverse(dto);
			FollowUpDataDto followUpDataDto = new FollowUpDataDto(dto, list.size());
			logger.info("Getting detiles is {} ", followUpDataDto);
			return ResponseEntity.ok(followUpDataDto);

		}
		logger.info("Detiles not found ");

		return null;

	}

	private List<FollowUpDto> getLimitedRowsBatchAndDate(List<List<Object>> values, String date, int startingIndex,
			int maxRows) {
		List<FollowUpDto> followUpDtos = new ArrayList<>();

		int endIndex = startingIndex + maxRows;

		ListIterator<List<Object>> iterator = values.listIterator(startingIndex);

		while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
			List<Object> row = iterator.next();

			if (row != null && !row.isEmpty()) {
				FollowUpDto followUpDto = wrapper.listToFollowUpDTO(row);
				System.out.println(followUpDto);
				if (followUpDto.getCallback().equalsIgnoreCase(date)) {
					followUpDtos.add(followUpDto);
				}
			}
		}
		return followUpDtos;
	}

	@Scheduled(fixedRate = 43200000) // 12 hours in milliseconds
	public void evictAllCaches() {
		// This method will be scheduled to run every 12 hours
		// and will evict all entries in the specified caches
	}

}
