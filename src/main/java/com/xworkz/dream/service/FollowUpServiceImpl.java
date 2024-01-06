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
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.StatusList;
import com.xworkz.dream.repository.FollowUpRepository;
import com.xworkz.dream.repository.RegisterRepository;
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

		FollowUpDto followUpDto = wrapper.setFollowUpEnwuiry(traineeDto);
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
		System.err.println(values + "                                " + range);
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
			HttpServletRequest request) throws IOException {
		log.info("Get follow-up by email service start. SpreadsheetId: {}, Email: {}", spreadsheetId, email);
		List<List<Object>> data = repo.getFollowUpDetails(spreadsheetId);
		FollowUpDto followUp = data.stream().filter(list -> list.size() > 2 && list.get(2) instanceof String)
				.filter(list -> ((String) list.get(2)).equalsIgnoreCase(email)
						&& list.get(14).toString().equalsIgnoreCase("Active"))
				.findFirst().map(wrapper::listToFollowUpDTO).orElse(null);

		if (followUp != null) {
			log.info("Follow-up details found for email: {}", email);
			return ResponseEntity.ok(followUp);
		} else {
			log.info("Follow-up details not found for email: {}", email);
			return ResponseEntity.ok(followUp);
		}
	}

	@Override
	public ResponseEntity<FollowUpDataDto> getFollowUpDetails(String spreadsheetId, int startingIndex, int maxRows,
			String status, String courseName, String date) throws IOException {
		log.info("Get Follow-up Details service start. SpreadsheetId: {}, StartingIndex: {}, MaxRows: {}, Status: {}, "
				+ "CourseName: {}, Date: {}", spreadsheetId, startingIndex, maxRows, status, courseName, date);
		List<FollowUpDto> followUpDto = new ArrayList<FollowUpDto>();
		List<List<Object>> lists = repo.getFollowUpDetails(spreadsheetId);
		List<List<Object>> traineeData = repository.readData(spreadsheetId);
		System.out.println(traineeData + "              " + status);

		if (status != null && !status.isEmpty() && lists != null) {
			if (status.toString().equalsIgnoreCase(Status.ENQUIRY.toString())) {
				List<List<Object>> data = lists.stream()
						.filter(list -> list.stream()
								.anyMatch(value -> value != null && value.toString().equalsIgnoreCase(status)))
						.collect(Collectors.toList());

				List<FollowUpDto> dtos = new ArrayList<FollowUpDto>();
				if (data != null) {
					data.stream().forEach(items -> {
						FollowUpDto dto = wrapper.listToFollowUpDTO(items);
						TraineeDto traineedto = getTraineeDtoByEmail(traineeData, dto.getBasicInfo().getEmail());
						if (traineedto != null) {
							dto.setCourseName(traineedto.getCourseInfo().getCourse());
						}
						dtos.add(dto);

					});
					log.debug("Pagination data: {}", data);
					if (!courseName.equalsIgnoreCase("null")) {
						List<FollowUpDto> filterData = dtos.stream()
								.filter(item -> item.getCourseName().equalsIgnoreCase(courseName))
								.collect(Collectors.toList());
						List<FollowUpDto> finalList = filterData.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalList, filterData.size());
						return ResponseEntity.ok(followUpDataDto);

					} else {
						List<FollowUpDto> finalData = dtos.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalData, dtos.size());
						return ResponseEntity.ok(followUpDataDto);
					}
				}
			} else if (status.toString().equalsIgnoreCase(Status.NEW.toString())) {
				List<List<Object>> data = lists.stream()
						.filter(list -> list.stream()
								.anyMatch(value -> value != null && value.toString().equalsIgnoreCase(status)))
						.collect(Collectors.toList());

				List<FollowUpDto> dtos = new ArrayList<FollowUpDto>();
				if (data != null) {
					data.stream().forEach(items -> {
						FollowUpDto dto = wrapper.listToFollowUpDTO(items);
						TraineeDto traineedto = getTraineeDtoByEmail(traineeData, dto.getBasicInfo().getEmail());
						if (traineedto != null) {
							dto.setCourseName(traineedto.getCourseInfo().getCourse());
						}
						dtos.add(dto);

					});
					log.debug("Pagination data: {}", data);
					if (!courseName.equalsIgnoreCase("null")) {
						List<FollowUpDto> filterData = dtos.stream()
								.filter(item -> item.getCourseName().equalsIgnoreCase(courseName))
								.collect(Collectors.toList());
						List<FollowUpDto> finalList = filterData.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalList, filterData.size());
						return ResponseEntity.ok(followUpDataDto);

					} else {
						List<FollowUpDto> finalData = dtos.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalData, dtos.size());
						return ResponseEntity.ok(followUpDataDto);
					}
				}
			}

			else if (status.toString().equalsIgnoreCase(Status.Past_followup.toString().replace('_', ' ').toString())) {
				List<FollowUpDto> followUpDtos = new ArrayList<FollowUpDto>();
				StatusList statusList = new StatusList();
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				List<List<Object>> data = lists.stream()
						.filter(elements -> statusList.getStatusCheck().contains(elements.get(8).toString()))
						.collect(Collectors.toList());
				data.stream().forEach(items -> {
					FollowUpDto dto = wrapper.listToFollowUpDTO(items);
					if (dto.getCallback() != null && dto.getCallback().length() > 11
							&& !dto.getAdminDto().getUpdatedBy().equalsIgnoreCase("NA")
							&& (LocalDate.parse(LocalDateTime.parse(dto.getCallback()).format(dateFormatter)))
									.isBefore(LocalDate.now().minusDays(2))) {
						followUpDtos.add(dto);

					} else if (dto.getCallback() != null && dto.getCallback().length() < 11
							&& !dto.getAdminDto().getUpdatedBy().equalsIgnoreCase("NA")
							&& (LocalDate.parse(LocalDate.parse(dto.getCallback()).format(dateFormatter)))
									.isBefore(LocalDate.now().minusDays(2))) {
						followUpDtos.add(dto);
					}

				});
				if (followUpDto != null) {
					followUpDto = followUpDtos.stream().sorted(Comparator.comparing(FollowUpDto::getId).reversed())
							.skip(startingIndex).limit(maxRows).collect(Collectors.toList());
					followUpDtos.stream().forEach(dto -> {
						TraineeDto traineedto = getTraineeDtoByEmail(traineeData, dto.getBasicInfo().getEmail());
						if (traineedto != null) {
							dto.setCourseName(traineedto.getCourseInfo().getCourse());
						}

					});

					log.debug("Pagination data: {}", followUpDto);
					if (!courseName.equalsIgnoreCase("null")) {
						List<FollowUpDto> filterData = followUpDtos.stream()
								.filter(item -> item.getCourseName().equalsIgnoreCase(courseName))
								.collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(filterData, filterData.size());
						return ResponseEntity.ok(followUpDataDto);

					} else {
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(followUpDto, followUpDtos.size());
						return ResponseEntity.ok(followUpDataDto);
					}

				}
			}

			else if (status.toString()
					.equalsIgnoreCase(Status.Never_followUp.toString().replace('_', ' ').toString())) {
				List<FollowUpDto> followUpDtos = new ArrayList<FollowUpDto>();
				StatusList statusList = new StatusList();
				List<List<Object>> data = lists.stream()
						.filter(elements -> statusList.getStatusCheck().contains(elements.get(8)))
						.collect(Collectors.toList());
				data.stream().forEach(items -> {
					FollowUpDto dto = wrapper.listToFollowUpDTO(items);
					if (dto.getCurrentlyFollowedBy().equalsIgnoreCase("NONE")) {
						followUpDtos.add(dto);
					}
				});
				if (followUpDtos != null) {
					followUpDto = followUpDtos.stream().sorted(Comparator.comparing(FollowUpDto::getId).reversed())
							.skip(startingIndex).limit(maxRows).collect(Collectors.toList());
					followUpDtos.stream().forEach(dto -> {
						TraineeDto traineedto = getTraineeDtoByEmail(traineeData, dto.getBasicInfo().getEmail());
						if (traineedto != null) {
							dto.setCourseName(traineedto.getCourseInfo().getCourse());
						}

					});

					log.debug("Pagination data: {}", followUpDtos);
					if (!courseName.equalsIgnoreCase("null")) {
						List<FollowUpDto> filterData = followUpDtos.stream()
								.filter(item -> item.getCourseName().equalsIgnoreCase(courseName))
								.collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(filterData, filterData.size());
						return ResponseEntity.ok(followUpDataDto);

					} else {
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(followUpDto, followUpDtos.size());
						return ResponseEntity.ok(followUpDataDto);
					}

				}
			}

			else if (status.toString().equalsIgnoreCase(Status.Joined.toString())) {
				List<List<Object>> data = lists.stream()
						.filter(list -> list.stream()
								.anyMatch(value -> value != null && value.toString().equalsIgnoreCase(status)))
						.collect(Collectors.toList());
				List<FollowUpDto> dtos = new ArrayList<FollowUpDto>();
				if (data != null) {
					data.stream().forEach(items -> {
						FollowUpDto dto = wrapper.listToFollowUpDTO(items);
						TraineeDto traineedto = getTraineeDtoByEmail(traineeData, dto.getBasicInfo().getEmail());
						if (traineedto != null) {
							dto.setCourseName(traineedto.getCourseInfo().getCourse());
						}
						dtos.add(dto);

					});
					log.debug("Pagination data: {}", data);
					if (!courseName.equalsIgnoreCase("null")) {
						List<FollowUpDto> filterData = dtos.stream()
								.filter(item -> item.getCourseName().equalsIgnoreCase(courseName))
								.collect(Collectors.toList());
						List<FollowUpDto> finalList = filterData.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalList, filterData.size());
						return ResponseEntity.ok(followUpDataDto);

					} else {
						List<FollowUpDto> finalData = dtos.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalData, dtos.size());
						return ResponseEntity.ok(followUpDataDto);
					}
				}
			} else if (status.toString().equalsIgnoreCase(Status.RNR.toString())) {
				List<List<Object>> data = lists.stream().filter(list -> list.stream()
						.anyMatch(value -> value != null && value.toString().equalsIgnoreCase(Status.Busy.toString())
								|| value.toString().equalsIgnoreCase(
										Status.Incomingcall_not_available.toString().replace('_', ' '))
								|| value.toString().equalsIgnoreCase(Status.Not_reachable.toString().replace('_', ' '))
								|| value.toString().equalsIgnoreCase(Status.RNR.toString())
								|| value.toString().equalsIgnoreCase(Status.Call_Drop.toString().replace('_', ' '))))
						.collect(Collectors.toList());

				List<FollowUpDto> dtos = new ArrayList<FollowUpDto>();
				if (data != null) {
					data.stream().forEach(items -> {
						FollowUpDto dto = wrapper.listToFollowUpDTO(items);
						TraineeDto traineedto = getTraineeDtoByEmail(traineeData, dto.getBasicInfo().getEmail());
						if (traineedto != null) {
							dto.setCourseName(traineedto.getCourseInfo().getCourse());
						}
						dtos.add(dto);

					});
					log.debug("Pagination data: {}", data);
					if (!courseName.equalsIgnoreCase("null")) {
						List<FollowUpDto> filterData = dtos.stream()
								.filter(item -> item.getCourseName().equalsIgnoreCase(courseName))
								.collect(Collectors.toList());
						List<FollowUpDto> finalList = filterData.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalList, filterData.size());
						return ResponseEntity.ok(followUpDataDto);

					} else {
						List<FollowUpDto> finalData = dtos.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalData, dtos.size());
						return ResponseEntity.ok(followUpDataDto);
					}
				}
			} else if (status.toString().equalsIgnoreCase(Status.Not_interested.toString().replace('_', ' '))) {
				List<List<Object>> data = lists.stream().filter(list -> list.stream().anyMatch(value -> value != null
						&& value.toString().equalsIgnoreCase(Status.Not_interested.toString().replace('_', ' '))
						|| value.toString().equalsIgnoreCase(Status.Drop_After_Course.toString().replace('_', ' '))
						|| value.toString().equalsIgnoreCase(Status.Drop_After_Placement.toString().replace('_', ' '))
						|| value.toString().equalsIgnoreCase(Status.Higher_studies.toString().replace('_', ' '))
						|| value.toString().equalsIgnoreCase(Status.Joined_other_institute.toString().replace('_', ' '))
						|| value.toString().equalsIgnoreCase(Status.Not_joining.toString().replace('_', ' '))
						|| value.toString().equalsIgnoreCase(Status.Wrong_number.toString().replace('_', ' '))))
						.collect(Collectors.toList());

				List<FollowUpDto> dtos = new ArrayList<FollowUpDto>();
				if (data != null) {
					data.stream().forEach(items -> {
						FollowUpDto dto = wrapper.listToFollowUpDTO(items);
						TraineeDto traineedto = getTraineeDtoByEmail(traineeData, dto.getBasicInfo().getEmail());
						if (traineedto != null) {
							dto.setCourseName(traineedto.getCourseInfo().getCourse());
						}
						dtos.add(dto);

					});
					log.debug("Pagination data: {}", data);
					if (!courseName.equalsIgnoreCase("null")) {
						List<FollowUpDto> filterData = dtos.stream()
								.filter(item -> item.getCourseName().equalsIgnoreCase(courseName))
								.collect(Collectors.toList());
						List<FollowUpDto> finalList = filterData.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalList, filterData.size());
						return ResponseEntity.ok(followUpDataDto);

					} else {
						List<FollowUpDto> finalData = dtos.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalData, dtos.size());
						return ResponseEntity.ok(followUpDataDto);
					}
				}
			} else if (status.toString().equalsIgnoreCase(Status.Interested.toString())) {
				List<List<Object>> data = lists.stream()
						.filter(list -> list.stream().anyMatch(value -> value != null
								&& value.toString().equalsIgnoreCase(Status.Let_us_know.toString().replace('_', ' '))
								|| value.toString().equalsIgnoreCase(Status.Need_online.toString().replace('_', ' '))
								|| value.toString().equalsIgnoreCase(Status.Joining.toString())
								|| value.toString().equalsIgnoreCase(Status.Interested.toString())))
						.collect(Collectors.toList());

				List<FollowUpDto> dtos = new ArrayList<FollowUpDto>();
				if (data != null) {
					data.stream().forEach(items -> {
						FollowUpDto dto = wrapper.listToFollowUpDTO(items);
						TraineeDto traineedto = getTraineeDtoByEmail(traineeData, dto.getBasicInfo().getEmail());
						if (traineedto != null) {
							dto.setCourseName(traineedto.getCourseInfo().getCourse());
						}
						dtos.add(dto);

					});
					log.debug("Pagination data: {}", data);
					if (!courseName.equalsIgnoreCase("null")) {
						List<FollowUpDto> filterData = dtos.stream()
								.filter(item -> item.getCourseName().equalsIgnoreCase(courseName))
								.collect(Collectors.toList());
						List<FollowUpDto> finalList = filterData.stream()
								.sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(finalList, filterData.size());
						return ResponseEntity.ok(followUpDataDto);

					} else {
						dtos.stream().sorted(Comparator.comparing(FollowUpDto::getId).reversed()).skip(startingIndex)
								.limit(maxRows).collect(Collectors.toList());
						FollowUpDataDto followUpDataDto = new FollowUpDataDto(dtos, data.size());
						return ResponseEntity.ok(followUpDataDto);
					}
				}
			} else {
				log.warn("Follow-up data not found.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} else {
			log.warn("Bad request. Status is null or empty.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		return null;
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
			HttpServletRequest request) throws IOException {
		log.info("Get Status Details service start. SpreadsheetId: {}, StartingIndex: {}, MaxRows: {}, Email: {}",
				spreadsheetId, startingIndex, maxRows, email);
		List<StatusDto> statusDto = new ArrayList<>();
		List<List<Object>> dataList = repo.getFollowUpStatusDetails(spreadsheetId);
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
						.filter(list -> list.stream()
								.anyMatch(value -> value.toString().equalsIgnoreCase(email)
										&& list.get(14).toString().equalsIgnoreCase("Active")))
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

	private List<FollowUpDto> getLimitedRowsBatchAndDate(List<List<Object>> values, String date, int startingIndex,
			int maxRows) {
		log.info("Get Limited Rows Batch and Date service start. Date: {}, StartingIndex: {}, MaxRows: {}", date,
				startingIndex, maxRows);
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
		log.debug("Limited rows batch and date: {}", followUpDtos);
		return followUpDtos;
	}

	@Override
	public ResponseEntity<FollowUpDataDto> getFollowStatusByDate(String date, int startIndex, int endIndex,
			String spreadsheetID, HttpServletRequest request) throws IOException {
		log.info("Get Follow Status By Date service start. Date: {}, StartIndex: {}, EndIndex: {}, SpreadsheetID: {}",
				date, startIndex, endIndex, spreadsheetID);
		List<List<Object>> dataList = repo.getFollowupStatusByDate(spreadsheetID);

		if (dataList != null && date != null) {
			List<List<Object>> list = dataList.stream()
					.filter(item -> item.get(9).equals(date) && item.get(14).toString().equalsIgnoreCase("Active")
							&& !item.get(8).toString().equalsIgnoreCase(Status.Joined.toString())
							&& !item.get(8).toString()
									.equalsIgnoreCase(Status.Not_interested.toString().replace("_", " ")))
					.collect(Collectors.toList());
			List<FollowUpDto> dto = getLimitedRowsBatchAndDate(list, date, startIndex, endIndex);
			Collections.reverse(dto);
			FollowUpDataDto followUpDataDto = new FollowUpDataDto(dto, list.size());
			log.info("Getting details: {}", followUpDataDto);
			return ResponseEntity.ok(followUpDataDto);

		}
		log.info("Details not found");
		return null;

	}

	@Override
	public FollowUpDataDto getTraineeDetailsByCourseInFollowUp(String spreadsheetId, String courseName,
			int startingIndex, int maxIndex) throws IOException {
		log.info("Get Trainee Details By Course In FollowUp service start. SpreadsheetId: {}, CourseName: {}, "
				+ "StartingIndex: {}, MaxIndex: {}", spreadsheetId, courseName, startingIndex, maxIndex);

		FollowUpDataDto followUpDataDto = new FollowUpDataDto(Collections.emptyList(), 0);
		try {
			List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> traineeData = repository.readData(spreadsheetId);
			log.debug("Null check for all the data: {}", followUpDataDto);
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

	public List<FollowUpDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows) {
		List<FollowUpDto> dto = new ArrayList<>();
		log.info("Get Limited Rows service start. StartingIndex: {}, MaxRows: {}", startingIndex, maxRows);
		if (values != null) {
			int endIndex = Math.min(startingIndex + maxRows, values.size());

			dto = values.subList(startingIndex, endIndex).stream().filter(row -> row != null && !row.isEmpty())
					.map(wrapper::listToFollowUpDTO).collect(Collectors.toList());
		}
		log.debug("Returning values with pagination: {}", dto);
		return dto;
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
