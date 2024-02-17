package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.StatusList;
import com.xworkz.dream.repository.FollowUpRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.service.util.FollowUpUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class FollowUpServiceImpl implements FollowUpService {

	@Autowired
	private FollowUpRepository repo;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private RegisterRepository repository;
	@Value("${login.sheetId}")
	private String id;
	@Value("${login.teamFile}")
	private String userFile;
	@Value("${sheets.rowStartRange}")
	private String rowStartRange;
	@Value("${sheets.rowEndRange}")
	private String rowEndRange;
	@Value("${sheets.followUpRowCurrentStartRange}")
	private String followUpRowCurrentStartRange;
	@Value("${sheets.followUpRowCurrentEndRange}")
	private String followUpRowCurrentEndRange;
	@Value("${sheets.followUpSheetName}")
	private String followUpSheetName;
	@Value("${sheets.followUprowStartRange}")
	private String followUprowStartRange;
	@Value("${sheets.followUprowEndRange}")
	private String followUprowEndRange;
	@Autowired
	private CacheService cacheService;
	private static final Logger log = LoggerFactory.getLogger(FollowUpServiceImpl.class);
	@Autowired
	private FollowUpUtil followUpUtil;

	@Override
	public boolean addToFollowUp(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException {
		log.info("Follow-up service running for traineeDto: {}", traineeDto);
		if (traineeDto == null) {
			return false;
		}

		FollowUpDto followUpDto = wrapper.setFollowUp(traineeDto);
		if (followUpDto == null) {
			log.warn("TraineeDto is null. Follow-up service aborted.");
			return false;
		}
		if (followUpDto.getCallback() == null) {
			followUpDto.setCallback(LocalDate.now().plusDays(1).toString());
		}
		List<Object> data = wrapper.extractDtoDetails(followUpDto);
		if (data == null) {
			log.warn("Data is null. Follow-up service aborted.");
			return false;
		}
		log.info("Saving data to the follow-up sheet: {}", data);
		repo.saveToFollowUp(spreadSheetId, data);
		log.info("Adding FollowUp details to Cache: {}", data);
		cacheService.addFollowUpToCache("followUpDetails", spreadSheetId, data);
		log.info("Follow-up service completed successfully");
		return true;
	}

	@Override
	public boolean addToFollowUpEnquiry(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException {
		log.info("Follow-up Enquiry service running for traineeDto: {}", traineeDto);
		if (traineeDto == null) {
			return false;
		}

		FollowUpDto followUpDto = wrapper.setFollowUpEnquiry(traineeDto);
		if (followUpDto == null) {
			log.info("Follow-up Enquiry service running for traineeDto: {}", traineeDto);
			return false;
		}
		if (followUpDto.getCallback() == null) {
			followUpDto.setCallback(LocalDate.now().plusDays(1).toString());
		}
		List<Object> data = wrapper.extractDtoDetails(followUpDto);
		if (data == null) {
			log.warn("Data is null. Follow-up Enquiry service aborted.");
			return false;
		}
		boolean save = repo.saveToFollowUp(spreadSheetId, data);
		cacheService.addFollowUpToCache("followUpDetails", spreadSheetId, data);
		log.info("Follow-up Enquiry service completed successfully");
		return save;

	}

	@Override
	public boolean addCsrToFollowUp(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException {
		log.info("CSR Follow-up service running for traineeDto: {}", traineeDto);
		if (traineeDto == null) {
			return false;
		}

		FollowUpDto followUpDto = wrapper.setFollowUpCSR(traineeDto);
		if (followUpDto == null) {
			log.info("CSR Follow-up Enquiry service running for traineeDto: {}", traineeDto);
			return false;
		}
		if (followUpDto.getCallback() == null) {
			followUpDto.setCallback(LocalDate.now().plusDays(1).toString());
		}
		List<Object> data = wrapper.extractDtoDetails(followUpDto);
		if (data == null) {
			log.warn("Data is null. CSR Follow-up  service aborted.");
			return false;
		}
		boolean save = repo.saveToFollowUp(spreadSheetId, data);
		cacheService.addFollowUpToCache("followUpDetails", spreadSheetId, data);
		log.info("CSR Follow-up  service completed successfully");
		return save;

	}

	private int findByEmailForUpdate(String spreadsheetId, String email) throws IOException {

		ValueRange data = repo.getEmailList(spreadsheetId);
		List<List<Object>> values = data.getValues();
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				List<Object> row = values.get(i);
				if (row.size() > 0 && row.get(0).toString().equalsIgnoreCase(email)) {
					log.debug("Row index found for email: {}. Index: {}", email, i + 2);
					return i + 2;
				}
			}
		}
		log.debug("Row index not found for email: {}", email);
		return -1;
	}

	@Override
	public boolean updateFollowUp(String spreadsheetId, String email, TraineeDto dto)
			throws IOException, IllegalAccessException {
		log.info("Update follow-up service running. SpreadsheetId: {}, Email: {}", spreadsheetId, email);
		FollowUpDto followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);
		if (followUpDto == null) {
			log.warn("FollowUpDto is null. Update follow-up service aborted.");
			return false;
		}

		int rowIndex = findByEmailForUpdate(spreadsheetId, email);
		if (rowIndex != -1) {
			String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;
			followUpDto.getBasicInfo().setTraineeName(dto.getBasicInfo().getTraineeName());
			followUpDto.getBasicInfo().setEmail(dto.getBasicInfo().getEmail());
			followUpDto.getBasicInfo().setContactNumber(dto.getBasicInfo().getContactNumber());
			followUpDto.setAdminDto(dto.getAdminDto());
			List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followUpDto));
			ValueRange valueRange = new ValueRange();

			if (!values.isEmpty()) {
				List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
				values.set(0, modifiedValues);
			}
			valueRange.setValues(values);
			UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);
			cacheService.updateCacheFollowUp("followUpDetails", spreadsheetId, email, followUpDto);

			if (updated != null && !updated.isEmpty()) {
				log.info("Follow-up details updated successfully");
				return true;
			} else {
				log.warn("Failed to update follow-up details");
				return false;
			}
		} else {
			log.warn("Row index not found for email: {}. Update follow-up service aborted.", email);
			return false;
		}
	}

	@Override
	public boolean updateCurrentFollowUp(String calBack, String spreadsheetId, String email, String currentStatus,
			String currentlyFollowedBy, String joiningDate) throws IOException, IllegalAccessException {

		log.info("Update current follow-up service running. SpreadsheetId: {}, Email: {}", spreadsheetId, email);
		FollowUpDto followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);
		int rowIndex = findByEmailForUpdate(spreadsheetId, email);
		if (rowIndex != -1) {
			String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;
			UpdateValuesResponse updated = setFollowUpDto(calBack, spreadsheetId, currentStatus, currentlyFollowedBy,
					followUpDto, joiningDate, range);
			cacheService.updateCacheFollowUp("followUpDetails", spreadsheetId, email, followUpDto);
			if (updated != null && !updated.isEmpty()) {
				log.info("Current follow-up details updated successfully");
				return true;
			} else {
				log.warn("Failed to update current follow-up details");
				return false;
			}
		} else {
			log.warn("Row index not found for email: {}. Update current follow-up service aborted.", email);
			return false;
		}
	}

	private UpdateValuesResponse setFollowUpDto(String callBack, String spreadsheetId, String currentStatus,
			String currentlyFollowedBy, FollowUpDto followUpDto, String joiningDate, String range)
			throws IllegalAccessException, IOException {
		log.info("Setting follow-up DTO. SpreadsheetId: {}, Email: {}", spreadsheetId,
				followUpDto.getBasicInfo().getEmail());
		AuditDto existingAdminDto = followUpDto.getAdminDto();
		AuditDto adminDto = new AuditDto();
		if (existingAdminDto != null) {
			adminDto.setCreatedBy(existingAdminDto.getCreatedBy());
			adminDto.setCreatedOn(existingAdminDto.getCreatedOn());
		}
		followUpDto.setCurrentlyFollowedBy(currentlyFollowedBy);
		if (currentStatus != null && !currentStatus.equals("NA")) {
			followUpDto.setCurrentStatus(currentStatus);
		}
		if (joiningDate != null && !joiningDate.equals("NA")) {
			followUpDto.setJoiningDate(joiningDate);
		}

		adminDto.setUpdatedBy(currentlyFollowedBy);
		adminDto.setUpdatedOn(LocalDateTime.now().toString());
		if (callBack != null && !callBack.equals("NA")) {
			if (LocalDate.now().isEqual(LocalDate.parse(callBack))) {
				followUpDto.setCallback(callBack);
				followUpDto.setFlag("InActive");

			} else {
				followUpDto.setCallback(callBack);
				followUpDto.setFlag("Active");

			}
		}
		if (callBack != null && callBack.equals("NA")) {
			followUpDto.setCallback(LocalDate.now().plusDays(1).toString());
			followUpDto.setFlag("Active");

		}
		followUpDto.setAdminDto(adminDto);
		followUpDto.setCourseName("NA");
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followUpDto));

		if (!values.isEmpty()) {
			List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
			values.set(0, modifiedValues);

		}
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		log.error(values + "                                " + range);
		UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);
		cacheService.updateCacheFollowUp("followUpDetails", spreadsheetId, followUpDto.getBasicInfo().getEmail(),
				followUpDto);
		log.info("Follow-up DTO set successfully");
		return updated;
	}

	@Override
	public ResponseEntity<String> updateFollowUpStatus(String spreadsheetId, StatusDto statusDto) {
		try {
			log.info("Update follow-up status service start. SpreadsheetId: {}, StatusDto: {}", spreadsheetId,
					statusDto);
			List<List<Object>> data = repo.getStatusId(spreadsheetId).getValues();
			StatusDto sdto = wrapper.setFollowUpStatus(statusDto, data);

			List<Object> statusData = wrapper.extractDtoDetails(sdto);
			boolean status = repo.updateFollowUpStatus(spreadsheetId, statusData);
			cacheService.updateFollowUpStatusInCache("followUpStatusDetails", spreadsheetId, statusData);

			if (status == true) {
				updateCurrentFollowUp(statusDto.getCallBack(), spreadsheetId, statusDto.getBasicInfo().getEmail(),
						statusDto.getAttemptStatus(), statusDto.getAttemptedBy(), statusDto.getJoiningDate());
				cacheService.updateFollowUpStatus("followUpDetails", spreadsheetId, statusDto);
			}
			log.info("Follow-up status updated successfully for ID: {}", statusDto.getId());
			return ResponseEntity.ok("Follow Status Updated for ID :  " + statusDto.getId());
		} catch (IOException | IllegalAccessException e) {
			log.error("An error occurred while updating follow-up status", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred with credentials file ");
		}
	}

	@Override
	public ResponseEntity<FollowUpDto> getFollowUpByEmail(String spreadsheetId, String email,
			HttpServletRequest request) {
		log.info("Get follow-up by email service start. SpreadsheetId: {}, Email: {}", spreadsheetId, email);
		List<List<Object>> data;
		try {
			data = repo.getFollowUpDetails(spreadsheetId);
			FollowUpDto followUp = data.stream().filter(list -> list.get(2).toString().equalsIgnoreCase(email))
					.findFirst().map(wrapper::listToFollowUpDTO).orElse(null);

			if (followUp != null) {
				log.info("Follow-up details found for email: {}", email);
				return ResponseEntity.ok(followUp);
			} else {
				log.info("Follow-up details not found for email: {}", email);
				return ResponseEntity.ok(followUp);
			}
		} catch (Exception e) {
			log.error("error fetching data from repo {} ", e);
			return null;
		}
	}

	@Override
	public FollowUpDataDto getFollowUpDetails(String spreadsheetId, int startingIndex, int maxRows, String status,
			String courseName, String date, String collegeName) {
		log.info(
				"Get Follow-up Details service start. SpreadsheetId: {}, StartingIndex: {}, MaxRows: {}, Status: {}, "
						+ "CourseName: {}, Date: {} ,CollegeName : {}  ",
				spreadsheetId, startingIndex, maxRows, status, courseName, date, collegeName);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		try {
			List<List<Object>> followUpList = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> traineeData = repository.readData(spreadsheetId);
			StatusList statusList = new StatusList();
			Predicate<FollowUpDto> predicate = predicateByStatus(status, courseName, date, collegeName, dateFormatter,
					statusList);
			try {
				List<FollowUpDto> dto = followUpUtil.getFollowupList(followUpList, traineeData).stream()
						.filter(predicate).sorted(Comparator.comparing(FollowUpDto::getRegistrationDate).reversed())
						.collect(Collectors.toList());
				List<FollowUpDto> limitedRows = dto.stream().skip(startingIndex).limit(maxRows)
						.collect(Collectors.toList());

				FollowUpDataDto followUpDataDto = new FollowUpDataDto(limitedRows, dto.size());
				return followUpDataDto;
			} catch (Exception e) {
				log.error("filter data fecthing error {} ", e);
			}
		} catch (Exception e) {
			log.error("sheet data fetchinng ceeor {}", e);
		}
		return null;

	}

	private Predicate<FollowUpDto> predicateByStatus(String status, String courseName, String date, String collegeName,
			DateTimeFormatter dateFormatter, StatusList statusList) {
		Predicate<FollowUpDto> predicate = null;

		if (!courseName.equals("null") && status.equals("null") && date.equals("null") && collegeName.equals("null")) {
			predicate = followUpData -> followUpData.getCourseName().equalsIgnoreCase(courseName);
		}
		if (!date.equals("null") && status.equals("null") && collegeName.equals("null") && courseName.equals("null")) {
			predicate = followUpData -> followUpData.getCallback().equalsIgnoreCase(date);
		}

		if (!collegeName.equals("null") && date.equals("null") && status.equals("null") && courseName.equals("null")) {
			predicate = followUpData -> followUpData.getCollegeName().equalsIgnoreCase(collegeName);
		}

		if (!collegeName.equals("null") && !date.equals("null") && status.equals("null") && courseName.equals("null")) {
			predicate = followUpData -> followUpData.getCollegeName().equalsIgnoreCase(collegeName)
					&& followUpData.getCallback().equalsIgnoreCase(date);
		}
		if (!courseName.equals("null") && !date.equals("null") && status.equals("null") && collegeName.equals("null")) {
			predicate = followUpData -> followUpData.getCourseName().equalsIgnoreCase(courseName)
					&& followUpData.getCallback().equalsIgnoreCase(date);
		}
		if (!courseName.equals("null") && !collegeName.equals("null") && status.equals("null") && date.equals("null")) {
			predicate = followUpData -> followUpData.getCourseName().equalsIgnoreCase(courseName)
					&& followUpData.getCollegeName().equalsIgnoreCase(collegeName);
		}

		predicate = followUpUtil.byStatus(status, courseName, date, collegeName, dateFormatter, statusList, predicate);
		predicate = followUpUtil.byStatusAndCourseName(status, courseName, date, collegeName, dateFormatter, statusList,
				predicate);
		predicate = followUpUtil.byStatusAndDate(status, courseName, date, collegeName, dateFormatter, statusList,
				predicate);
		predicate = followUpUtil.byStatusAndCollegeName(status, courseName, date, collegeName, dateFormatter,
				statusList, predicate);
		predicate = followUpUtil.byStatusAndCourseNameAndCollegeName(status, courseName, date, collegeName,
				dateFormatter, statusList, predicate);
		predicate = followUpUtil.byStatusCourseNameAndDateAndCollegeName(status, courseName, date, collegeName,
				dateFormatter, statusList, predicate);
		predicate = followUpUtil.byStatusAndDateAndCollegeName(status, courseName, date, collegeName, dateFormatter,
				statusList, predicate);

		predicate = followUpUtil.byStatusAndCourseAndDate(status, courseName, date, collegeName, dateFormatter,
				statusList, predicate);

		return predicate;
	}

	@Override
	public List<FollowUpDto> getFollowUpRows(List<List<Object>> values, int startingIndex, int maxRows) {
		log.info("Get Follow-up Rows service start. StartingIndex: {}, MaxRows: {}", startingIndex, maxRows);
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
		log.debug("Follow-up rows: {}", followUpDtos);
		return followUpDtos;
	}

	@Override
	public List<StatusDto> getStatusDetails(String spreadsheetId, int startingIndex, int maxRows, String email,
			HttpServletRequest request) {
		log.info("Get Status Details service start. SpreadsheetId: {}, StartingIndex: {}, MaxRows: {}, Email: {}",
				spreadsheetId, startingIndex, maxRows, email);
		List<StatusDto> statusDto = new ArrayList<>();
		List<List<Object>> dataList;
		dataList = repo.getFollowUpStatusDetails(spreadsheetId);

		List<List<Object>> data = dataList.stream()
				.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(email)))
				.collect(Collectors.toList());
		statusDto = getFollowUpStatusData(data, startingIndex, maxRows);
		log.debug("Status details: {}", statusDto);
		return statusDto;

	}

	@Override
	public List<StatusDto> getStatusDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException {
		log.info("Get Status Details by Email service start. SpreadsheetId: {}, Email: {}", spreadsheetId, email);
		List<StatusDto> statusDto = new ArrayList<>();
		List<List<Object>> dataList = repo.getFollowUpStatusDetails(spreadsheetId);
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
		log.debug("Status details by email: {}", statusDto);
		return statusDto;
	}

	@Override
	public List<StatusDto> getFollowUpStatusData(List<List<Object>> values, int startingIndex, int maxRows) {
		log.info("Get Follow-up Status Data service start. StartingIndex: {}, MaxRows: {}", startingIndex, maxRows);
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
		log.debug("Follow-up status data: {}", statusDtos);
		return statusDtos;
	}

	@Override
	public FollowUpDto getFollowUpDetailsByEmail(String spreadsheetId, String email) throws IOException {
		log.info("Get Follow-up Details by Email service start. SpreadsheetId: {}, Email: {}", spreadsheetId, email);
		FollowUpDto followUpDto = new FollowUpDto();

		if (email != null && !email.isEmpty()) {
			List<List<Object>> lists = repo.getFollowUpDetails(spreadsheetId);

			if (!lists.isEmpty()) {
				List<List<Object>> data = lists.stream()
						.filter(items -> items.get(15).toString().equalsIgnoreCase("Active")
								&& items.get(2).toString().equalsIgnoreCase(email))
						.collect(Collectors.toList());

				for (List<Object> list : data) {
					followUpDto = wrapper.listToFollowUpDTO(list);
				}
				log.debug("Follow-up details by email: {}", followUpDto);
				return followUpDto;
			}
		}
		return null;
	}

	@Override
	public ResponseEntity<String> updateFollowUp(String spreadsheetId, String email, FollowUpDto followDto)
			throws IOException, IllegalAccessException {
		log.info("Update Follow-up service start. SpreadsheetId: {}, Email: {}", spreadsheetId, email);
		int rowIndex = findByEmailForUpdate(spreadsheetId, email);

		String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followDto));
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);
		if (updated.isEmpty()) {
			log.error("Failed to update follow-up. SpreadsheetId: {}, Email: {}", spreadsheetId, email);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");
		} else {
			log.info("Follow-up updated successfully. SpreadsheetId: {}, Email: {}", spreadsheetId, email);
			return ResponseEntity.ok("Updated Successfully");
		}
	}

	@Override
	public FollowUpDataDto getTraineeDetailsByCourseInFollowUp(String spreadsheetId, String courseName,
			int startingIndex, int maxIndex) throws IOException {
		log.info("Get Trainee Details By Course In FollowUp service start. SpreadsheetId: {}, CourseName: {}, "
				+ "StartingIndex: {}, MaxIndex: {}", spreadsheetId, courseName, startingIndex, maxIndex);

		FollowUpDataDto followUpDataDto = new FollowUpDataDto(Collections.emptyList(), 0);
		
			List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> traineeData = repository.readData(spreadsheetId);
			log.debug("Null check for all the data: {}", followUpDataDto);
			if (Stream.of(followUpData, traineeData, spreadsheetId, courseName, repo, wrapper)
					.anyMatch(Objects::isNull)) {
				return followUpDataDto;
			}
			return getDataByCourseName(spreadsheetId, courseName, traineeData, startingIndex, maxIndex);
	}

	private FollowUpDto assignValuesToFollowUp(TraineeDto dto, FollowUpDto followUp) {
		log.debug("Assigning values to FollowUpDto. TraineeDto: {}, FollowUpDto: {}", dto, followUp);
		FollowUpDto fdto = new FollowUpDto();
		fdto.setId(dto.getId());
		fdto.setBasicInfo(dto.getBasicInfo());
		if (dto.getCourseInfo() != null) {
			fdto.setCourseName(dto.getCourseInfo().getCourse());
		}
		fdto.setCallback(followUp.getCallback());
		fdto.setCurrentlyFollowedBy(followUp.getCurrentlyFollowedBy());
		fdto.setCurrentStatus(followUp.getCurrentStatus());
		fdto.setJoiningDate(followUp.getJoiningDate());
		fdto.setRegistrationDate(followUp.getRegistrationDate());
		log.debug("Assigned values: {}", fdto);
		return fdto;
	}

	private FollowUpDataDto getDataByCourseName(String spreadsheetId, String courseName, List<List<Object>> traineeData,
			int startingIndex, int maxRows) {
		log.info("Get Data By Course Name service start. SpreadsheetId: {}, CourseName: {}, "
				+ "StartingIndex: {}, MaxRows: {}", spreadsheetId, courseName, startingIndex, maxRows);
		List<FollowUpDto> followUpDto = traineeData.stream()
				.filter(row -> row != null && row.size() > 9 && row.contains(courseName)).map(row -> {
					TraineeDto dto = wrapper.listToDto(row);
					if (dto == null) {
						return null;
					}
					FollowUpDto followUp = null;
					try {
						String email = dto.getBasicInfo().getEmail();
						log.debug("Attempting to get FollowUp details for email: {}", email);
						followUp = getFollowUpDetailsByEmail(spreadsheetId, email);

					} catch (IOException e) {
						log.error("An IOException occurred: " + e.getMessage(), e);
					}
					if (followUp == null) {
						return null;
					}

					FollowUpDto fdto = assignValuesToFollowUp(dto, followUp);
					return fdto;
				}).filter(Objects::nonNull).sorted(Comparator.comparing(FollowUpDto::getRegistrationDate))
				.collect(Collectors.toList());

		List<FollowUpDto> limitedRows = getPaginationData(followUpDto, startingIndex, maxRows);

		log.debug("Original followUpDto: {}", followUpDto);

		FollowUpDataDto dto = new FollowUpDataDto(limitedRows, limitedRows.size());
		return dto;
	}

	public List<FollowUpDto> getPaginationData(List<FollowUpDto> values, int startingIndex, int maxRows) {
		List<FollowUpDto> dto = new ArrayList<>();
		log.info("Get Pagination Data service start. StartingIndex: {}, MaxRows: {}", startingIndex, maxRows);

		if (values != null) {
			int endIndex = Math.min(startingIndex + maxRows, values.size());

			dto = values.subList(startingIndex, endIndex).stream()
					.sorted(Comparator.comparing(FollowUpDto::getRegistrationDate)).collect(Collectors.toList());
		}
		log.debug("Returning values with pagination: {}", dto);
		return dto;
	}

}
