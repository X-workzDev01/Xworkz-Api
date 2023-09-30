package com.xworkz.dream.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.FollowUp;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AdminDto;
import com.xworkz.dream.dto.BasicInfoDto;
import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
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
	private FollowUpDto followUpDto;
	private BatchDetails batch;

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
	private static final String API_KEY = "live_7dffc6f2186bc1a4bb21";

	private static final Logger logger = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws MessagingException, TemplateException {


		try {
			if (true) {// isCookieValid(request)
				List<List<Object>> data = repo.getIds(spreadsheetId).getValues();
				int size = data.size();

				dto.setId(size += 1);
				dto.getOthersDto().setXworkzEmail(Status.NA.toString());
				dto.getOthersDto().setPreferredLocation(Status.NA.toString());
				dto.getOthersDto().setPreferredClassType(Status.NA.toString());
				dto.getOthersDto().setSendWhatsAppLink(Status.NO.toString());
				dto.getOthersDto().setRegistrationDate(LocalDate.now().toString());
				dto.getAdminDto().setCreatedOn(LocalDateTime.now().toString());
				if (dto.getOthersDto().getReferalName() == null) {
					dto.getOthersDto().setReferalName("NA");

				}
				if (dto.getOthersDto().getComments() == null) {
					dto.getOthersDto().setComments("NA");
				}
				if (dto.getOthersDto().getWorking() == null) {

					dto.getOthersDto().setWorking("No");
				}
				if (dto.getOthersDto().getReferalContactNumber() == null) {

					dto.getOthersDto().setReferalContactNumber(0L);
				}

				List<Object> list = wrapper.extractDtoDetails(dto);
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

	@Override
	public boolean addToFollowUp(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException {
		FollowUpDto followUpDto = wrapper.setFollowUp(traineeDto);
		List<Object> data = wrapper.extractDtoDetails(followUpDto);
		repo.saveToFollowUp(spreadSheetId, data);
		return true;
	}

	@Override
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
					return true;
				}
			}
		}
		return false;
	}

	@Override
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

	@Override
	public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows) {
		try {
			List<List<Object>> dataList = repo.readData(spreadsheetId);

//			List<List<Object>> sortedData = dataList.stream()
//					.sorted(Comparator.comparing(list -> list.get(25).toString(), Comparator.reverseOrder()))
//					.collect(Collectors.toList());

			List<List<Object>> sortedData = dataList.stream()
					.sorted(Comparator.comparing(list -> list.get(24).toString(), Comparator.reverseOrder()))
					.collect(Collectors.toList());
			List<TraineeDto> dtos = getLimitedRows(sortedData, startingIndex, maxRows);
			HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getResponse();

			SheetsDto dto = new SheetsDto(dtos, dataList.size());

			return ResponseEntity.ok(dto);
		} catch (IOException e) {

			e.printStackTrace();

		}
		return null;

	}

	@Override
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

	@Override
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

	@Override
	public ResponseEntity<String> update(String spreadsheetId, String email, TraineeDto dto) {

		AdminDto admin = new AdminDto();
		admin.setCreatedBy(dto.getAdminDto().getCreatedBy());
		admin.setCreatedOn(dto.getAdminDto().getCreatedOn());
		admin.setUpdatedBy(dto.getAdminDto().getUpdatedBy());
		admin.setUpdatedOn(LocalDateTime.now().toString());
		dto.setAdminDto(admin);
		try {
			int rowIndex = findRowIndexByEmail(spreadsheetId, email);
			String range = traineeSheetName + rowStartRange + rowIndex + ":" + rowEndRange + rowIndex;
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

	@Override
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

	@Override

	public boolean updateCurrentFollowUp(String spreadsheetId, String email, String currentStatus,
			String currentlyFollowedBy, String joiningDate) throws IOException, IllegalAccessException {
		FollowUpDto followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);
		int rowIndex = findByEmailForUpdate(spreadsheetId, email);
		String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;
		UpdateValuesResponse updated = setFollowUpDto(spreadsheetId, currentStatus, currentlyFollowedBy, followUpDto,
				joiningDate, range);
		if (updated.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	private UpdateValuesResponse setFollowUpDto(String spreadsheetId, String currentStatus, String currentlyFollowedBy,
			FollowUpDto followUpDto, String joiningDate, String range) throws IllegalAccessException, IOException {
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
		followUpDto.setAdminDto(adminDto);
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followUpDto));
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);
		return updated;
	}

	@Override
	public ResponseEntity<String> updateFollowUpStatus(String spreadsheetId, StatusDto statusDto,
			HttpServletRequest request) {
		try {

			List<List<Object>> data = repo.getStatusId(spreadsheetId).getValues();
			StatusDto sdto = wrapper.setFollowUpStatus(statusDto, data);
			List<Object> statusData = wrapper.extractDtoDetails(sdto);
			boolean status = repo.updateFollowUpStatus(spreadsheetId, statusData);
			if (status == true) {

				boolean update = updateCurrentFollowUp(spreadsheetId, statusDto.getBasicInfo().getEmail(),
						statusDto.getAttemptStatus(), statusDto.getAttemptedBy(), statusDto.getJoiningDate());
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

	@Override
	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(String spreadsheetId, String value,
			HttpServletRequest request) {
		// SuggestionDto sDto = new SuggestionDto();
		// String values=value.toLowerCase();
		String pattern = ".{3}";
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
				e.printStackTrace();
			}
			return null;
		}
		return null;
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
	public ResponseEntity<FollowUpDto> getFollowUpByEmail(String spreadsheetId, String email,
			HttpServletRequest request) throws IOException {
		List<List<Object>> data = repo.getFollowUpDetails(spreadsheetId);
		FollowUpDto followUp = data.stream()
				.filter(list -> list.size() > 2 && list.get(2).toString().equalsIgnoreCase(email)).findFirst()
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

		if (status != null && !status.isEmpty()) {

			List<List<Object>> lists = repo.getFollowUpDetails(spreadsheetId);

			List<List<Object>> data = lists.stream()
					.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(status)))
					.collect(Collectors.toList());

			List<List<Object>> sortedData = data.stream()
					.sorted(Comparator.comparing(list -> list.get(4).toString(), Comparator.reverseOrder()))
					.collect(Collectors.toList());

			followUpDto = getFollowUpRows(sortedData, startingIndex, maxRows);
			FollowUpDataDto followUpDataDto = new FollowUpDataDto(followUpDto, data.size());
			repo.evictAllCachesOnTraineeDetails();
			return ResponseEntity.ok(followUpDataDto);
		}
		return null;
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
		List<List<Object>> data = dataList.stream()
				.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(email)))
				.collect(Collectors.toList());
		for (List<Object> row : data) {
			StatusDto dto = wrapper.listToStatusDto(row);
			statusDto.add(dto);
		}
		repo.evictAllCachesOnTraineeDetails();
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

	@Override
	public ResponseEntity<String> saveBirthDayInfo(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws IllegalAccessException, IOException {
		BirthDayInfoDto birthday = new BirthDayInfoDto();
		List<List<Object>> data = repo.getBirthDayId(spreadsheetId).getValues();
		int size = data.size();
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
			notification(id, loginEmail, teamList, request);

		} catch (IOException e) {
			throw new RuntimeException("Exception occurred: " + e.getMessage(), e);
		}

	}

	@Override
	public void notification(String spreadsheetId, String email, List<Team> teamList, HttpServletRequest requests)
			throws IOException {
		List<String> statusCheck = Stream.of(Status.Busy.toString(), Status.New.toString(),
				Status.Interested.toString(), Status.RNR.toString(), Status.Not_interested.toString().replace('_', ' '),
				Status.Incomingcall_not_available.toString().replace('_', ' '),
				Status.Not_reachable.toString().replace('_', ' '), Status.Let_us_know.toString().replace('_', ' '),
				Status.Need_online.toString().replace('_', ' ')).collect(Collectors.toList());

		LocalTime time = LocalTime.of(18, 00, 01, 500_000_000);
		List<StatusDto> notificationStatus = new ArrayList<StatusDto>();
		List<StatusDto> notificationStatusBymail = new ArrayList<StatusDto>();
		List<List<Object>> followup = repo.getFollowUpDetailsByid(spreadsheetId);
		followup.stream().forEach(f -> {
			followUpDto = wrapper.listToFollowUpDTO(f);
		});

		if (spreadsheetId != null) {
			List<List<Object>> listOfData = repo.notification(spreadsheetId);
			List<List<Object>> list = listOfData.stream().filter(items -> !items.contains("NA"))
					.collect(Collectors.toList());
			if (!list.isEmpty()) {
				if (email != null) {
					list.stream().forEach(e -> {
						StatusDto dto = wrapper.listToStatusDto(e);

						if (LocalDate.now().isEqual(LocalDate.parse(dto.getCallBack()))
								&& email.equalsIgnoreCase(dto.getAttemptedBy())
								&& statusCheck.contains(dto.getAttemptStatus())) {
							notificationStatusBymail.add(dto);
							response = ResponseEntity.ok(notificationStatusBymail);
						}
						if (LocalDate.now().minusDays(1).isEqual(LocalDate.parse(dto.getCallBack()))
								&& email.equalsIgnoreCase(dto.getAttemptedBy())
								&& statusCheck.contains(dto.getAttemptStatus())) {
							notificationStatusBymail.add(dto);

						}
						if (LocalDate.now().plusDays(1).isEqual(LocalDate.parse(dto.getCallBack()))
								&& email.equalsIgnoreCase(dto.getAttemptedBy())
								&& statusCheck.contains(dto.getAttemptStatus())) {
							notificationStatusBymail.add(dto);

						}

					});

				}

				list.stream().forEach(e -> {
					StatusDto dto = wrapper.listToStatusDto(e);
					if (dto.getCallBack() != null) {
						if (LocalDateTime.now().isAfter(LocalDateTime.of((LocalDate.parse(dto.getCallBack())), time))
								&& LocalDateTime.now().isBefore(
										LocalDateTime.of((LocalDate.parse(dto.getCallBack())), time.plusMinutes(26)))) {

							if (statusCheck.contains(dto.getAttemptStatus())
									&& LocalDate.now().isEqual(LocalDate.parse(dto.getCallBack()))) {

								notificationStatus.add(dto);
								response = ResponseEntity.ok(notificationStatus);

							}

						}
					}

				});
			}
			if (LocalTime.now().isAfter(time) && LocalTime.now().isBefore(time.plusMinutes(26))) {

				if (!notificationStatus.isEmpty()) {

					util.sendNotificationToEmail(teamList, notificationStatus);

				}

			}

		}

	}

	@Override
	public ResponseEntity<List<StatusDto>> setNotification(@Value("${myapp.scheduled.param}") String email,
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
	@CacheEvict(value = { "sheetsData", "emailData", "contactData", "getDropdowns", "followUpStatusDetails",
			"followUpDetails" }, allEntries = true)
	@Scheduled(fixedDelay = 43200000) // 12 hours in milliseconds
	public void evictAllCaches() {
		// This method will be scheduled to run every 12 hours
		// and will evict all entries in the specified caches
	}

	@Override
	public boolean updateCurrentFollowUp(String spreadsheetId, String email, String currentStatus,
			String currentlyFollowedBy) throws IOException, IllegalAccessException {
		// TODO Auto-generated method stub
		return false;
	}
}
