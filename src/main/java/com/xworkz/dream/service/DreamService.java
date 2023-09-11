package com.xworkz.dream.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jni.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.CourseDto;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.constants.FollowUp;
import com.xworkz.dream.dto.BasicInfoDto;
import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.Dropdown;
import com.xworkz.dream.dto.utils.Team;
import com.xworkz.dream.dto.utils.User;
import com.xworkz.dream.interfaces.EmailableClient;
import com.xworkz.dream.repo.DreamRepo;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;
import freemarker.template.TemplateException;

@Service

public class DreamService {

	@Autowired
	private DreamRepo repo;
	@Autowired
	private DreamWrapper wrapper;
	private FollowUpDto followUpDto;

	@Autowired
	private DreamUtil util;
	private String attemptedBy;
	private String id = "1p3G4et36vkzSDs3W63cj6qnUFEWljLos2HHXIZd78Gg";
	private HttpServletRequest request;
	private ResponseEntity<List<StatusDto>> response;
	private String loginEmail;
	List<Team> users = new ArrayList<Team>();
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
	private static final String API_KEY = "live_ace635e7c497dc70359f";

	private static final Logger logger = LoggerFactory.getLogger(DreamService.class);

//	@Autowired
//    public DreamService(EmailableClient emailableClient) {
//        this.emailableClient = emailableClient;
//    }

	// Rest of your code...
	public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws MessagingException, TemplateException {
		try {
			if (true) {// isCookieValid(request)
				List<List<Object>> data = repo.getIds(spreadsheetId).getValues();
				int size = data.size();
				System.out.println(size);

				dto.setId(size += 1);
				System.out.println(dto.getId());

				List<Object> list = wrapper.extractDtoDetails(dto);
//				for (Object object : list) {
//					System.out.println(object);
//				}

				boolean writeStatus = repo.writeData(spreadsheetId, list);
				// calling method to store date of birth details
				saveBirthDayInfo(spreadsheetId, dto, request);
				if (writeStatus) {
					logger.info("Data written successfully to spreadsheetId: {}", spreadsheetId);
					boolean status = addToFollowUp(dto, spreadsheetId);
					if (status) {
						logger.info("Data written successfully to spreadsheetId and Added to Follow Up: {}",
								spreadsheetId);

//						boolean sent = util.sendCourseContent(dto.getBasicInfo().getEmail(),
//								dto.getBasicInfo().getTraineeName());
//						repo.evictAllCachesOnTraineeDetails();
//						if (sent == true) {
//							return ResponseEntity.ok("Data written successfully , Added to follow Up , sended course content ");
//						} else {
//							return ResponseEntity.ok("Email not sent, Data written successfully , Added to follow Up");
//						}
						boolean sent = util.sendCourseContent(dto.getBasicInfo().getEmail(),
								dto.getBasicInfo().getTraineeName());
						repo.evictAllCachesOnTraineeDetails();
						if (sent == true) {
							return ResponseEntity
									.ok("Data written successfully , Added to follow Up , sended course content ");
						} else {
							return ResponseEntity.ok("Email not sent, Data written successfully , Added to follow Up");
						}
					}
					return ResponseEntity.ok("Data written successfully , not added to Follow Up");
				} else {
					logger.warn("Failed to write data to spreadsheetId: {}", spreadsheetId);
					return ResponseEntity.badRequest().body("Failed to write data");
				}
			}
		} catch (IOException e) {
			logger.error("Error occurred while writing data to spreadsheetId: {}", spreadsheetId, e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Data mapping error");

		}
		return null;
	}

	public boolean addToFollowUp(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException {
		FollowUpDto followUpDto = new FollowUpDto();

		BasicInfoDto basicInfo = new BasicInfoDto();
		basicInfo.setTraineeName(traineeDto.getBasicInfo().getTraineeName());
		basicInfo.setEmail(traineeDto.getBasicInfo().getEmail());
		basicInfo.setContactNumber(traineeDto.getBasicInfo().getContactNumber());

		// Set the initialized BasicInfo object to followUpDto
		followUpDto.setBasicInfo(basicInfo);

		followUpDto.setCourseName(traineeDto.getCourseInfo().getCourse());
		followUpDto.setRegistrationDate(LocalDate.now().toString());
		followUpDto.setJoiningDate(FollowUp.NOT_CONFIRMED.toString());
		followUpDto.setId(traineeDto.getId());
		followUpDto.setCurrentlyFollowedBy(FollowUp.NONE.toString());
		followUpDto.setCurrentStatus(FollowUp.NEW.toString());
		List<Object> data = wrapper.extractDtoDetails(followUpDto);
		repo.saveToFollowUp(spreadSheetId, data);
		return true;
	}

	public ResponseEntity<String> emailCheck(String spreadsheetId, String email, HttpServletRequest request) {

		try {
			if (true) {// isCookieValid(request)
				ValueRange values = repo.getEmails(spreadsheetId);
				if (values.getValues() != null) {
					for (List<Object> row : values.getValues()) {

						if (row.get(0).toString().equalsIgnoreCase(email)) {
							logger.info("Email exists in spreadsheetId: {}", spreadsheetId);
							return ResponseEntity.status(HttpStatus.CREATED).body("Email exists");
						}
					}
				}
				logger.info("Email does not exist in spreadsheetId: {}", spreadsheetId);
				return ResponseEntity.ok("Email does not exist");
			} else {
				// Invalid cookie
				logger.info("Invalid cookie in the request");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid cookie");
			}
		} catch (Exception e) {
			logger.error("An error occurred while checking email in spreadsheetId: {}", spreadsheetId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	private boolean isCookieValid(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("Xworkz")) {
					System.out.println("Cookie Valid");
					return true;
				}
			}
		}
		return false;
	}

	public ResponseEntity<String> contactNumberCheck(String spreadsheetId, Long contactNumber,
			HttpServletRequest request) {
		try {
			if (true) {// isCookieValid(request)
				ValueRange values = repo.getContactNumbers(spreadsheetId);
				if (values.getValues() != null) {
					for (List<Object> row : values.getValues()) {
						if (row.get(0).toString().equals(String.valueOf(contactNumber))) {
							logger.info("Contact Number exists in spreadsheetId: {}", spreadsheetId);
							return ResponseEntity.status(HttpStatus.CREATED).body("Contact Number exists");
						}
					}
				}
				logger.info("Contact Number does not exist in spreadsheetId: {}", spreadsheetId);
				return ResponseEntity.ok("Contact Number does not exist");
			} else {
				// Invalid cookie
				logger.info("Invalid cookie in the request");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid cookie");
			}
		} catch (Exception e) {
			logger.error("An error occurred while checking Contact Number in spreadsheetId: {}", spreadsheetId, e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	@CacheEvict(value = { "sheetsData", "emailData", "contactData", "getDropdowns", "followUpStatusDetails",
			"followUpDetails" }, allEntries = true)
	@Scheduled(fixedDelay = 43200000) // 12 hours in milliseconds
	public void evictAllCaches() {
		// This method will be scheduled to run every 12 hours
		// and will evict all entries in the specified caches
	}

	public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows) {
		try {
			List<List<Object>> data = repo.readData(spreadsheetId);
			// System.out.println(data.toString());
			List<TraineeDto> dtos = getLimitedRows(data, startingIndex, maxRows);
			// System.out.println(dtos.toString());
			HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getResponse();

			SheetsDto dto = new SheetsDto(dtos, data.size());
			return ResponseEntity.ok(dto);
		} catch (IOException e) {

			e.printStackTrace();

		}
		return null;

	}

	public List<TraineeDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows) {
		List<TraineeDto> traineeDtos = new ArrayList<>();

		int endIndex = startingIndex + maxRows;
		int rowCount = values.size();

		ListIterator<List<Object>> iterator = values.listIterator(startingIndex);

		while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
			List<Object> row = iterator.next();

			if (row != null && !row.isEmpty()) {
				TraineeDto traineeDto = wrapper.listToDto(row);
				traineeDtos.add(traineeDto);
			}
		}
		return traineeDtos;
	}

	public List<TraineeDto> filterData(String spreadsheetId, String searchValue) throws IOException {
		if (searchValue != null && !searchValue.isEmpty()) {
			List<List<Object>> data = repo.readData(spreadsheetId);
			List<List<Object>> filteredLists = data.stream()
					.filter(list -> list.stream()
							.anyMatch(value -> value.toString().toLowerCase().contains(searchValue.toLowerCase())))
					.collect(Collectors.toList());
			List<TraineeDto> flist = new ArrayList<TraineeDto>();
			for (List<Object> list2 : filteredLists) {
				TraineeDto dto = wrapper.listToDto(list2);
				flist.add(dto);
			}
			return flist;
		} else {
			return null;
		}
	}

	public ResponseEntity<String> update(String spreadsheetId, String email, TraineeDto dto) {
		try {
			int rowIndex = findRowIndexByEmail(spreadsheetId, email);
			String range = traineeSheetName + rowStartRange + rowIndex + ":" + rowEndRange + rowIndex;
			System.out.println(range);
			System.out.println(dto);
			try {
				List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(dto));

				ValueRange valueRange = new ValueRange();
				valueRange.setValues(values);
				UpdateValuesResponse updated = repo.update(spreadsheetId, range, valueRange);
				if (updated.isEmpty()) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");
				} else {
					repo.evictAllCachesOnTraineeDetails();
					return ResponseEntity.ok("Updated Successfully");
				}

			} catch (IllegalAccessException e) {

				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");

			}

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");

		}

	}

	public ResponseEntity<String> updateFollowUp(String spreadsheetId, String email, FollowUpDto followDto)
			throws IOException, IllegalAccessException {
		FollowUpDto followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);

		int rowIndex = findByEmailForUpdate(spreadsheetId, email);

		String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followDto));
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);
		if (updated.isEmpty()) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");
		} else {
			repo.evictAllCachesOnTraineeDetails();
			return ResponseEntity.ok("Updated Successfully");
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

	private int findFollowUpRowIndexById(String spreadsheetId, int id) throws IOException {

		List<List<Object>> values = repo.getFollowUpDetails(spreadsheetId);

		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				List<Object> row = values.get(i);
				if (row.size() > 0 && row.get(0).equals(String.valueOf(id))) {
					return i + 3;
				}
			}
		}
		return -1;
	}

	public boolean updateCurrentFollowUp(String spreadsheetId, String email, String currentStatus,
			String currentlyFollowedBy) throws IOException, IllegalAccessException {
		// List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
		FollowUpDto followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);
		System.out.println(followUpDto);
		int rowIndex = findByEmailForUpdate(spreadsheetId, email);
		String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;
		followUpDto.setCurrentStatus(currentStatus);
		// followUpDto.setCurrentlyFollowedBy(currentlyFollowedBy);;
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followUpDto));
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);
		if (updated.isEmpty()) {
			// repo.evictAllCachesOnTraineeDetails();
			return false;
		} else {
			// repo.evictAllCachesOnTraineeDetails();
			return true;
		}
	}

	public ResponseEntity<String> updateFollowUpStatus(String spreadsheetId, StatusDto statusDto,
			HttpServletRequest request) {
		try {

			List<List<Object>> data = repo.getStatusId(spreadsheetId).getValues();
			int size = data.size();
			System.out.println(size);
			BasicInfoDto basicInfo = new BasicInfoDto();
			basicInfo.setTraineeName(statusDto.getBasicInfo().getTraineeName());
			basicInfo.setEmail(statusDto.getBasicInfo().getEmail());

			StatusDto sdto = new StatusDto();
			sdto.setId(size += 1);
			sdto.setBasicInfo(basicInfo);
			sdto.setAttemptedOn(LocalDateTime.now().toString());
			sdto.setAttemptedBy(statusDto.getAttemptedBy());
			sdto.setAttemptStatus(statusDto.getAttemptStatus());
			sdto.setComments(statusDto.getComments());
			sdto.setCallDuration(statusDto.getCallDuration());
			sdto.setCallBack(statusDto.getCallBack());
			sdto.setCallBackTime(statusDto.getCallBackTime());
			sdto.setPreferredLocation(statusDto.getPreferredLocation());
			sdto.setPreferredClassType(statusDto.getPreferredClassType());
			List<Object> statusData = wrapper.extractDtoDetails(sdto);
			System.out.println(statusData.toString());

			boolean status = repo.updateFollowUpStatus(spreadsheetId, statusData);
			if (status == true) {
				System.out.println("this is current follow up");
				System.out.println(statusDto.getId());
				boolean update = updateCurrentFollowUp(spreadsheetId, statusDto.getBasicInfo().getEmail(),
						statusDto.getAttemptStatus(), statusDto.getAttemptedBy());
				System.out.println("update status:" + update);
				repo.evictAllCachesOnTraineeDetails();
			}

			return ResponseEntity.ok("Follow Status Updated for ID :  " + statusDto.getId());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred in Mapping data");

		} catch (IOException e) {

			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred with credentials file ");
		}

	}

	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(String spreadsheetId, String value,
			HttpServletRequest request) {
		// SuggestionDto sDto = new SuggestionDto();
		// String values=value.toLowerCase();
		String pattern = ".{3}";
		List<TraineeDto> suggestion = new ArrayList<>();
		if (value != null) {
			try {
				List<List<Object>> dataList = repo.getEmailsAndNames(spreadsheetId, value);
				System.out.println(dataList);
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
				e.printStackTrace();
			}
			return null;
		}
		return null;
	}

//	public static List<SuggestionDto> getSuggestions(String dataToMatch, List<List<Object>> data) {
//
//		List<Object> list = data.stream().flatMap(List::stream)
//				.filter(value -> value.toString().equalsIgnoreCase(dataToMatch)).collect(Collectors.toList());
//		System.out.println(list.toString());
//		return null;
//	}

	public ResponseEntity<?> getDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException {
		List<List<Object>> data = repo.readData(spreadsheetId);
		TraineeDto trainee = null;
		for (List<Object> list : data) {
			if (list.contains(email)) {
				trainee = wrapper.listToDto(list);
			}
		}
		if (trainee != null) {
			return ResponseEntity.ok(trainee);
		} else {
			return new ResponseEntity<>("Email Not Found", HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<FollowUpDto> getFollowUpByEmail(String spreadsheetId, String email,
			HttpServletRequest request) throws IOException {
		List<List<Object>> data = repo.getFollowUpDetails(spreadsheetId);
		FollowUpDto followUp = null;
		for (List<Object> list : data) {
			if (list.get(2).toString().equalsIgnoreCase(email)) {
				followUp = wrapper.listToFollowUpDTO(list);
			}
		}
		if (followUp != null) {
			return ResponseEntity.ok(followUp);
		} else {
			return ResponseEntity.ok(followUp);
		}
	}

	public ResponseEntity<FollowUpDataDto> getFollowUpDetails(String spreadsheetId, int startingIndex, int maxRows,
			String status) throws IOException {
		List<FollowUpDto> followUpDto = new ArrayList<FollowUpDto>();
		// String traineeStatus=status.toLowerCase();
		if (status != null && !status.isEmpty()) {

			List<List<Object>> lists = repo.getFollowUpDetails(spreadsheetId);

			List<List<Object>> data = lists.stream()
					.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(status)))
					.collect(Collectors.toList());
			followUpDto = getFollowUpRows(data, startingIndex, maxRows);
			FollowUpDataDto followUpDataDto = new FollowUpDataDto(followUpDto, data.size());
			repo.evictAllCachesOnTraineeDetails();
			return ResponseEntity.ok(followUpDataDto);
		}
		return null;
	}

	public List<FollowUpDto> getFollowUpRows(List<List<Object>> values, int startingIndex, int maxRows) {
		List<FollowUpDto> followUpDtos = new ArrayList<>();

		int endIndex = startingIndex + maxRows;
		System.out.println(
				"end row:" + endIndex + " " + " Start Index:" + " " + startingIndex + " " + " max index:" + maxRows);
		// int rowCount = values.size();

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

	public List<StatusDto> getStatusDetails(String spreadsheetId, int startingIndex, int maxRows, String email,
			HttpServletRequest request) throws IOException {
		List<StatusDto> statusDto = new ArrayList<>();
		List<List<Object>> dataList = repo.getFollowUpStatusDetails(spreadsheetId);
		System.out.println(dataList.toString());
		List<List<Object>> data = dataList.stream()
				.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(email)))
				.collect(Collectors.toList());
		statusDto = getFollowUpStatusData(data, startingIndex, maxRows);

		return statusDto;
	}

	public List<StatusDto> getStatusDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException {
		List<StatusDto> statusDto = new ArrayList<>();
		List<List<Object>> dataList = repo.getFollowUpStatusDetails(spreadsheetId);
		System.out.println(dataList.toString());
		List<List<Object>> data = dataList.stream()
				.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(email)))
				.collect(Collectors.toList());
		for (List<Object> row : data) {
			StatusDto dto = wrapper.listToStatusDto(row);
			statusDto.add(dto);
			System.out.println(dto);
		}
		repo.evictAllCachesOnTraineeDetails();
		return statusDto;
	}

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

	public ResponseEntity<String> saveDetails(String spreadsheetId, BatchDetailsDto dto, HttpServletRequest request)
			throws IOException, IllegalAccessException {
		List<List<Object>> data = repo.getBatchId(spreadsheetId).getValues();
		int size = data.size();
		dto.setId(size += 1);
		List<Object> list = wrapper.extractDtoDetails(dto);
		boolean save = repo.saveBatchDetails(spreadsheetId, list);
		if (save == true) {
			return ResponseEntity.ok("Batch details added successfully");
		} else {
			return ResponseEntity.ok("Batch details Not added");
		}
	}

	public ResponseEntity<String> saveBirthDayInfo(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws IllegalAccessException, IOException {
		BirthDayInfoDto birthday = new BirthDayInfoDto();
		List<List<Object>> data = repo.getBirthDayId(spreadsheetId).getValues();
		int size = data.size();
		birthday.setDto(dto.getBasicInfo());
		birthday.setId(size += 1);
		List<Object> list = wrapper.extractDtoDetails(birthday);
//		for (Object object : list) {
//			System.out.println(object);
//		}
		boolean save = repo.saveBirthDayDetails(spreadsheetId, list);
		if (save != false) {
			return ResponseEntity.ok("Birth day information added successfully");
		}
		return ResponseEntity.ok("Birth day information Not added");
	}

	// suhas
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

	// suhas
	public ResponseEntity<BatchDetails> getBatchDetailsByCourseName(String spreadsheetId, String courseName) {
		List<List<Object>> detailsByCourseName;
		try {
			detailsByCourseName = repo.getCourseDetails(spreadsheetId);
			BatchDetails batch = new BatchDetails();
			if (detailsByCourseName != null) {
				for (List<Object> row : detailsByCourseName) {
					if (row.get(1).toString().equalsIgnoreCase(courseName)) {
						batch.setId(Integer.valueOf(row.get(0).toString()));
						batch.setCourseName(String.valueOf(row.get(1)));
						batch.setTrainerName(String.valueOf(row.get(2)));
						batch.setStartTime(String.valueOf(row.get(3)));
						batch.setBatchType(String.valueOf(row.get(4)));
						batch.setTiming(String.valueOf(row.get(5)));
						batch.setBranch(String.valueOf(row.get(6)));
						batch.setStatus(String.valueOf(row.get(7)));

					}

				}
				return ResponseEntity.ok(batch);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	public FollowUpDto getFollowUpDetailsByEmail(String spreadsheetId, String email) throws IOException {
		// List<FollowUpDto> followUpDto = new ArrayList<FollowUpDto>();
		// String traineeStatus=status.toLowerCase();
		FollowUpDto followUpDto = new FollowUpDto();
		if (email != null && !email.isEmpty()) {
			List<List<Object>> lists = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> data = lists.stream()
					.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(email)))
					.collect(Collectors.toList());
			for (List<Object> list : data) {
				// System.out.println(list.toString());
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

	@Scheduled(fixedRate = 30 * 60 * 1000) // 1000 milliseconds = 1 seconds

	public void notification() {
		try {
			List<Team> teamList = getTeam();
			notification(id, loginEmail, teamList, request);

		} catch (IOException e) {
			throw new RuntimeException("Exception occurred: " + e.getMessage(), e);
		}

	}

	public void notification(String spreadsheetId, String email, List<Team> teamList, HttpServletRequest requests)
			throws IOException {

		List<String> statusCheck = Stream.of(Status.Busy.toString(), Status.New.toString(),
				Status.Interested.toString(), Status.RNR.toString(), Status.Not_interested.toString().replace('_', ' '),
				Status.Incomingcall_not_available.toString().replace('_', ' '),
				Status.Not_reachable.toString().replace('_', ' '), Status.Let_us_know.toString().replace('_', ' '),
				Status.Need_online.toString().replace('_', ' ')).collect(Collectors.toList());

		LocalTime time = LocalTime.of(18, 01, 01, 500_000_000);
		List<StatusDto> notificationStatus = new ArrayList<StatusDto>();
		List<StatusDto> notificationStatusBymail = new ArrayList<StatusDto>();  
		List<List<Object>> followup = repo.getFollowUpDetailsByid(spreadsheetId);
		followup.stream().forEach(f -> {
			followUpDto = wrapper.listToFollowUpDTO(f);
		});
		try {
			if (spreadsheetId != null) {
				List<List<Object>> list = repo.notification(spreadsheetId);

				if (email != null) {
					list.stream().forEach(e -> {
						StatusDto dto = wrapper.listToStatusDto(e);

						if (LocalDate.now().isEqual(LocalDate.parse(dto.getCallBack()))
								&& email.equalsIgnoreCase(dto.getAttemptedBy()) 
								&& statusCheck.contains(dto.getAttemptStatus())) {

							notificationStatusBymail.add(dto);
							response = ResponseEntity.ok(notificationStatusBymail);
						}
					});

				}
				list.stream().forEach(e -> {
					StatusDto dto = wrapper.listToStatusDto(e);

					if (LocalDateTime.now().isAfter(LocalDateTime.of((LocalDate.parse(dto.getCallBack())), time))
							&& LocalDateTime.now().isBefore(
									LocalDateTime.of((LocalDate.parse(dto.getCallBack())), time.plusMinutes(30)))) {

						if (statusCheck.contains(dto.getAttemptStatus())
								&& LocalDate.now().isEqual(LocalDate.parse(dto.getCallBack()))) {

							notificationStatus.add(dto);
							response = ResponseEntity.ok(notificationStatus);

						}

					}

				});
				if (LocalTime.now().isAfter(time) && LocalTime.now().isBefore(time.plusMinutes(30))) {

					if (!notificationStatus.isEmpty()) {

						util.sendNotificationToEmail(teamList, notificationStatus);
					} else {
						System.out.println("notification is not there");

					}

				}
			}

		} catch (

		IOException e) {
			e.printStackTrace();

		}

	}

	public ResponseEntity<List<StatusDto>> setNotification(@Value("${myapp.scheduled.param}") String email,
			@Value("${myapp.scheduled.param}") HttpServletRequest requests) throws IOException {
		this.request = requests;
		this.loginEmail = email;  
		notification();
		return response;

	}

	// suhas
	public String verifyEmails(String email) {
		return emailableClient.verifyEmail(email, API_KEY);
	}

}
