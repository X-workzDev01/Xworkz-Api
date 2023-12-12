package com.xworkz.dream.service;

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
import java.util.Objects;
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
import com.xworkz.dream.repository.FollowUpRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.wrapper.DreamWrapper;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

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
	private Logger log = LoggerFactory.getLogger(FollowUpServiceImpl.class);

	@Override
	public boolean addToFollowUp(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException {
		log.info("followup service running {}", traineeDto);
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
		log.info("saving data to the follow up sheet{}", data);
		repo.saveToFollowUp(spreadSheetId, data);
		log.info("add FollowUp details To Cache{}", data);
		cacheService.addFollowUpToCache("followUpDetails", spreadSheetId, data);
		return true;
	}

	@Override
	public boolean addToFollowUpEnquiry(TraineeDto traineeDto, String spreadSheetId)
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
		cacheService.addFollowUpToCache("followUpDetails", spreadSheetId, data);
		return save;

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

	@Override
	public boolean updateFollowUp(String spreadsheetId, String email, TraineeDto dto)
			throws IOException, IllegalAccessException {
		System.err.println("follow up data   ===================================               " + dto);

		FollowUpDto followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);
		System.out.println("777777777777777777777777777777777777777777777777         " + followUpDto);

		if (followUpDto == null) {
			return false;
		}

		int rowIndex = findByEmailForUpdate(spreadsheetId, email);
		System.err.println("333333333333333333333333333333333333333333333333333           " + rowIndex);
		if (rowIndex != -1) {
			String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;

//			followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);
			followUpDto.getBasicInfo().setEmail(dto.getBasicInfo().getEmail());
			followUpDto.getBasicInfo().setContactNumber(dto.getBasicInfo().getContactNumber());
			followUpDto.setAdminDto(dto.getAdminDto());
			List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followUpDto));
			ValueRange valueRange = new ValueRange();

			if (!values.isEmpty()) {
				List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
				values.set(0, modifiedValues); // Update the values list with the modified sublist
			}
			valueRange.setValues(values);
			System.err.println("tttttttttttttttttttttttttttttttttttttttttt                   " + values);
			UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);
			cacheService.updateCacheFollowUp("followUpDetails", spreadsheetId, email, followUpDto);

			if (updated != null && !updated.isEmpty()) {
				// repo.evictAllCachesOnTraineeDetails();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean updateCurrentFollowUp(String calBack, String spreadsheetId, String email, String currentStatus,
			String currentlyFollowedBy, String joiningDate) throws IOException, IllegalAccessException {
		FollowUpDto followUpDto = getFollowUpDetailsByEmail(spreadsheetId, email);
		int rowIndex = findByEmailForUpdate(spreadsheetId, email);
		if (rowIndex != -1) {
			String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;
			UpdateValuesResponse updated = setFollowUpDto(calBack, spreadsheetId, currentStatus, currentlyFollowedBy,
					followUpDto, joiningDate, range);
			cacheService.updateCacheFollowUp("followUpDetails", spreadsheetId, email, followUpDto);
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
			followUpDto.setCallback(LocalDateTime.of(LocalDate.parse(callBack), LocalTime.now()).toString());
		}
		if (callBack != null && callBack.equals("NA")) {
			followUpDto.setCallback(LocalDateTime.of(LocalDate.now(), LocalTime.now()).plusDays(1).toString());
		}
		followUpDto.setAdminDto(adminDto);
		followUpDto.setCourseName("NA");
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followUpDto));

		// removing id while update
		if (!values.isEmpty()) {
			List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
			values.set(0, modifiedValues); // Update the values list with the modified sublist

		}
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		UpdateValuesResponse updated = repo.updateFollow(spreadsheetId, range, valueRange);
		cacheService.updateCacheFollowUp("followUpDetails", spreadsheetId, followUpDto.getBasicInfo().getEmail(),
				followUpDto);
		return updated;
	}

	@Override
	public ResponseEntity<String> updateFollowUpStatus(String spreadsheetId, StatusDto statusDto) {
		try {
			List<List<Object>> data = repo.getStatusId(spreadsheetId).getValues();
			StatusDto sdto = wrapper.setFollowUpStatus(statusDto, data);

			List<Object> statusData = wrapper.extractDtoDetails(sdto);
			boolean status = repo.updateFollowUpStatus(spreadsheetId, statusData);
			cacheService.updateFollowUpStatusInCache("followUpStatusDetails", spreadsheetId, statusData);

			if (status == true) {
				updateCurrentFollowUp(statusDto.getCallBack(), spreadsheetId, statusDto.getBasicInfo().getEmail(),
						statusDto.getAttemptStatus(), statusDto.getAttemptedBy(), statusDto.getJoiningDate());
				// repo.evictAllCachesOnTraineeDetails();
				cacheService.updateFollowUpStatus("followUpDetails", spreadsheetId, statusDto);
			}
			return ResponseEntity.ok("Follow Status Updated for ID :  " + statusDto.getId());
		} catch (IOException | IllegalAccessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred with credentials file ");
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
			String status, String courseName, String date) throws IOException {
		List<FollowUpDto> followUpDto = new ArrayList<FollowUpDto>();
		List<List<Object>> lists = repo.getFollowUpDetails(spreadsheetId);
		List<List<Object>> traineeData = repository.readData(spreadsheetId);

		if (status != null && !status.isEmpty() && lists != null) {
			List<List<Object>> data = lists.stream().filter(
					list -> list.stream().anyMatch(value -> value != null && value.toString().equalsIgnoreCase(status)))
					.collect(Collectors.toList());
			if (data != null) {
				List<List<Object>> sortedData = data.stream().sorted(Comparator.comparing(
						list -> list != null && !list.isEmpty() && list.size() > 4 ? list.get(4).toString() : "",
						Comparator.reverseOrder())).collect(Collectors.toList());
				log.info("Runnung sorted data by followup {} ", sortedData);
				followUpDto = getFollowUpRows(sortedData, startingIndex, maxRows);
				// mapping course name from trainee table to follow up
				followUpDto.stream().forEach(dto -> {
					TraineeDto traineedto = getTraineeDtoByEmail(traineeData, dto.getBasicInfo().getEmail());
					if (traineedto != null) {
						dto.setCourseName(traineedto.getCourseInfo().getCourse());
					}

				});
				log.debug("Running service Pagination data {}" + followUpDto);
				if (!courseName.equalsIgnoreCase("null")) {
					List<FollowUpDto> filterData = followUpDto.stream()
							.filter(item -> item.getCourseName().equalsIgnoreCase(courseName))
							.collect(Collectors.toList());
					FollowUpDataDto followUpDataDto = new FollowUpDataDto(filterData, filterData.size());
					return ResponseEntity.ok(followUpDataDto);

				} else {
					FollowUpDataDto followUpDataDto = new FollowUpDataDto(followUpDto, data.size());
					return ResponseEntity.ok(followUpDataDto);
				}
				// repo.evictFollowUpStatusDetails();
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
	public FollowUpDto getFollowUpDetailsByEmail(String spreadsheetId, String email) throws IOException {

		FollowUpDto followUpDto = new FollowUpDto();
		if (email != null && !email.isEmpty()) {
			List<List<Object>> lists = repo.getFollowUpDetails(spreadsheetId);
			if (!lists.isEmpty()) {
				List<List<Object>> data = lists.stream()
						.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(email)))
						.collect(Collectors.toList());
				for (List<Object> list : data) {
					followUpDto = wrapper.listToFollowUpDTO(list);
				}

				return followUpDto;
			}
		}
		return null;
	}

	@Override
	public ResponseEntity<String> updateFollowUp(String spreadsheetId, String email, FollowUpDto followDto)
			throws IOException, IllegalAccessException {

		int rowIndex = findByEmailForUpdate(spreadsheetId, email);

		String range = followUpSheetName + followUprowStartRange + rowIndex + ":" + followUprowEndRange + rowIndex;
		System.out.println("00000000000000000000000000000000000000000             " + range);
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(followDto));
		System.out.println("=======================================           " + values);
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

	private List<FollowUpDto> getLimitedRowsBatchAndDate(List<List<Object>> values, String date, int startingIndex,
			int maxRows) {
		List<FollowUpDto> followUpDtos = new ArrayList<>();

		int endIndex = startingIndex + maxRows;

		ListIterator<List<Object>> iterator = values.listIterator(startingIndex);

		while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
			List<Object> row = iterator.next();

			if (row != null && !row.isEmpty()) {
				FollowUpDto followUpDto = wrapper.listToFollowUpDTO(row);
				if (followUpDto.getCallback().equalsIgnoreCase(date)) {
					followUpDtos.add(followUpDto);
				}
			}
		}
		return followUpDtos;
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
			log.info("Getting detiles is {} ", followUpDataDto);
			return ResponseEntity.ok(followUpDataDto);

		}
		log.info("Detiles not found ");

		return null;

	}

	@Override
	public FollowUpDataDto getTraineeDetailsByCourseInFollowUp(String spreadsheetId, String courseName,
			int startingIndex, int maxIndex) throws IOException {
		FollowUpDataDto followUpDataDto = new FollowUpDataDto(Collections.emptyList(), 0);
		try {
			List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> traineeData = repository.readData(spreadsheetId);
			log.debug("null check for all the data {}", followUpDataDto);
			if (Stream.of(followUpData, traineeData, spreadsheetId, courseName, repo, wrapper)
					.anyMatch(Objects::isNull)) {
				return followUpDataDto;
			}
			return getDataByCourseName(spreadsheetId, courseName, traineeData, startingIndex, maxIndex);
		} catch (IOException e) {
			log.error("An IOException occurred: " + e.getMessage(), e);
			return followUpDataDto;
		}
	}

	// for pagination
	public List<FollowUpDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows) {
		List<FollowUpDto> dto = new ArrayList<>();

		if (values != null) {
			int endIndex = Math.min(startingIndex + maxRows, values.size());

			dto = values.subList(startingIndex, endIndex).stream().filter(row -> row != null && !row.isEmpty())
					.map(wrapper::listToFollowUpDTO).collect(Collectors.toList());
		}
		log.debug("Returning values with pagination {}", dto);
		return dto;
	}

	private FollowUpDto assignValuesToFollowUp(TraineeDto dto, FollowUpDto followUp) {
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
		log.debug("assigned values {}", fdto);
		return fdto;
	}

	private FollowUpDataDto getDataByCourseName(String spreadsheetId, String courseName, List<List<Object>> traineeData,
			int startingIndex, int maxRows) {
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
						System.out.println(followUp);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (followUp == null) {
						return null;
					}

					FollowUpDto fdto = assignValuesToFollowUp(dto, followUp);
					return fdto;
				}).filter(Objects::nonNull).sorted(Comparator.comparing(FollowUpDto::getRegistrationDate))
				.collect(Collectors.toList());

		List<FollowUpDto> limitedRows = getPaginationData(followUpDto, startingIndex, maxRows);

		// Add logging statements for debugging
		log.debug("Original followUpDto: {}", followUpDto);

		FollowUpDataDto dto = new FollowUpDataDto(limitedRows, limitedRows.size());
		return dto;
	}

	public List<FollowUpDto> getPaginationData(List<FollowUpDto> values, int startingIndex, int maxRows) {
		List<FollowUpDto> dto = new ArrayList<>();

		if (values != null) {
			int endIndex = Math.min(startingIndex + maxRows, values.size());

			dto = values.subList(startingIndex, endIndex).stream()
					.sorted(Comparator.comparing(FollowUpDto::getRegistrationDate)).collect(Collectors.toList());
		}
		log.debug("Returning values with pagination {}", dto);
		return dto;
	}

}
