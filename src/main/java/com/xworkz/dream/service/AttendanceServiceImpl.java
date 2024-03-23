package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

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
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AbsentDaysDto;
import com.xworkz.dream.dto.AbsenteesDto;
import com.xworkz.dream.dto.AttendanceDataDto;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.AttendanceTrainee;
import com.xworkz.dream.dto.AttendanceViewDto;
import com.xworkz.dream.dto.BatchAttendanceDto;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.WrapperUtil;
import com.xworkz.dream.repository.AttendanceRepository;
import com.xworkz.dream.repository.FollowUpRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.BatchWrapper;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class AttendanceServiceImpl implements AttendanceService {
	@Value("${login.sheetId}")
	private String sheetId;
	@Value("${sheets.attendanceInfoRange}")
	private String attendanceInfoRange;
	@Value("${sheets.attendanceInfoIDRange}")
	private String attendanceInfoIDRange;
	@Value("${sheets.attendanceInfoStartRange}")
	private String attendanceStartRange;
	@Value("${sheets.attendanceInfoEndRange}")
	private String attendanceEndRange;
	@Value("${sheets.attandanceInfoSheetName}")
	private String attandanceInfoSheetName;
	@Value("${sheets.attandenceNameAndCourseRange}")
	private String attandenceNameAndCourseRange;
	@Autowired
	private WrapperUtil util;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private BatchWrapper batchWrapper;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private BatchService batchService;
	@Autowired
	private BatchAttendanceService batchAttendanceService;
	@Autowired
	private FollowUpRepository repository;
	@Autowired
	private AttendanceRepository attendanceRepository;
	@Autowired
	private RegisterRepository registerRepository;

	@Autowired
	private DreamUtil dreamUtil;

	private static Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);

	@Override
	public ResponseEntity<String> writeAttendance(String spreadsheetId, AttendanceDto dto, HttpServletRequest request) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Set<ConstraintViolation<AttendanceDto>> violation = factory.getValidator().validate(dto);
		try {
			Boolean traineeAlreadyAdded = this.traineeAlreadyAdded(dto.getCourse(), dto.getId());
			if (traineeAlreadyAdded == false) {
				if (violation.isEmpty() && dto != null) {
					if (dto.getAttemptStatus().equalsIgnoreCase(Status.Joined.toString())
							&& !dto.getCourse().equals(Status.NA.toString())) {
						wrapper.setValueAttendaceDto(dto);
						List<Object> list;

						list = util.extractDtoDetails(dto);
						list.remove(4);
						attendanceRepository.writeAttendance(spreadsheetId, list, attendanceInfoRange);
						cacheService.addAttendancdeToCache("attendanceData", "listOfAttendance", list);
						log.info("Attendance Detiles Added Sucessfully SpreadsheetId: {} , Detiles: {} ", spreadsheetId,
								dto.toString());

						return ResponseEntity.ok("Attendance Detiles Added Sucessfully");

					}
				} else {
					return ResponseEntity.ok("Trainee Already Added To Attendance");
				}

			}
		} catch (Exception e) {
			log.error("Error accessing traineeAlreadyAdded method: {}", e.getMessage());
		}
		return ResponseEntity.ok("Attendance detiles does not added ");

	}

	@Override
	public Boolean traineeAlreadyAdded(String courseName, Integer id) {
		List<AttendanceDto> absentListByBatch;

		absentListByBatch = this.getAbsentListByBatch(courseName);
		if (absentListByBatch != null) {
			boolean anyMatch = absentListByBatch.stream().anyMatch(dto -> {
				if (dto.getId() != null) {
					Integer dtoId = dto.getId();
					return dtoId != null && dtoId.equals(id);
				}
				return false;
			});
			return anyMatch;
		}
		return false;

	}

	@Override
	public List<String> markAndSaveAbsentDetails(List<AbsenteesDto> absentDtoList, String batch) {
		List<String> name = new ArrayList<String>();
		try {

			List<List<Object>> attendanceList = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
			List<List<Object>> filteredList = attendanceList.stream().filter(entry -> batch.equals(entry.get(3)))
					.collect(Collectors.toList());
			this.markTrainerAttendance(batch, true);
			for (List<Object> values : filteredList) {
				AttendanceDto attendanceDto = wrapper.attendanceListToDto(values);
				Boolean checkAbsentDate = this.checkAbsentDate(attendanceDto);
				if (checkAbsentDate == false) {
					for (AbsenteesDto dto : absentDtoList) {
						updateAttendanceDetails(attendanceDto, dto, batch);

					}

				} else {
					name.add(attendanceDto.getTraineeName());
				}

			}
			return name;
		} catch (IOException | IllegalAccessException e) {
			log.error("Error accessing attendance data or updating attendance details: {}", e.getMessage());
		}
		return null;

	}

	private void updateAttendanceDetails(AttendanceDto attendanceDto, AbsenteesDto dto, String batch)
			throws IOException, IllegalAccessException {
		if (attendanceDto.getId() != null && dto.getId().equals(attendanceDto.getId())) {

			int rowIndex = findByID(sheetId, dto.getId());
			String range = attandanceInfoSheetName + attendanceStartRange + rowIndex + ":" + attendanceEndRange
					+ rowIndex;

			updateAbsentDatesAndReasons(attendanceDto, dto);
			updateTotalAbsent(attendanceDto, dto);
			List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(attendanceDto));
			ValueRange valueRange = new ValueRange();
			if (!values.isEmpty()) {
				List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(0, values.get(0).size()));
				modifiedValues.remove(0);
				values.set(0, modifiedValues);
				log.debug("values {}", values);
			}
			valueRange.setValues(values);
			UpdateValuesResponse update = attendanceRepository.update(sheetId, range, valueRange);
			cacheService.updateCacheAttendancde("attendanceData", "listOfAttendance", attendanceDto.getId(),
					attendanceDto);
			if (!update.isEmpty()) {
				attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
				sendAbsentMail(dto.getId(), batch, dto.getReason());
				log.info("Attendance updated successfully : {} ", update);
			} else {
				log.debug("Attendance Not Updated : {} ", update);
			}
		}

	}

	private Boolean checkAbsentDate(AttendanceDto attendanceDto) {
		String absentDate = attendanceDto.getAbsentDate();
		String[] split = absentDate.split(",");
		for (String date : split) {
			if (date.equals(LocalDate.now().toString())) {
				return true;
			}
		}
		return false;

	}

	private void updateAbsentDatesAndReasons(AttendanceDto attendanceDto, AbsenteesDto dto) {
		String currentAbsentDates = attendanceDto.getAbsentDate();
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String newAbsentDate = localDate.format(formatter);
		String updatedAbsentDates = !currentAbsentDates.equals(null)
				&& currentAbsentDates.contains(Status.NA.toString()) ? newAbsentDate
						: currentAbsentDates + "," + newAbsentDate;

		attendanceDto.setAbsentDate(updatedAbsentDates);

		String currentReason = attendanceDto.getReason();
		String newReason = dto.getReason();
		String updatedReasons = !currentReason.equals(null) && currentReason.contains(Status.NA.toString()) ? newReason
				: currentReason + "," + newReason;
		attendanceDto.setReason(updatedReasons);
		attendanceDto.getAdminDto().setUpdatedBy(dto.getUpdatedBy());
		attendanceDto.getAdminDto().setUpdatedOn(LocalDate.now().toString());
		log.info("Absent dates and reasons updated: " + attendanceDto.getAbsentDate() + ", "
				+ attendanceDto.getReason());

	}

	private void updateTotalAbsent(AttendanceDto attendanceDto, AbsenteesDto dto) {
		Integer currentAbsent = attendanceDto.getTotalAbsent();
		Integer updateAbsent = !currentAbsent.equals(null) && currentAbsent.equals(0) ? 1 : currentAbsent + 1;
		attendanceDto.setTotalAbsent(updateAbsent);
		log.info("Total absent updated: " + attendanceDto.getTotalAbsent());
	}

	private int findByID(String spreadsheetId, Integer id) throws IOException {

		List<List<Object>> data = attendanceRepository.getAttendanceData(spreadsheetId, attendanceInfoIDRange);
		if (data != null) {
			if (data != null && !data.isEmpty()) {
				for (int i = 0; i < data.size(); i++) {
					List<Object> row = data.get(i);
					if (row.size() > 0 && row.get(1).toString().equals(id.toString())) {
						return i + 2;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public List<AttendanceTrainee> getTrainee(String batch) {
		List<AttendanceTrainee> traineeInfoList = new ArrayList<>();
		List<List<Object>> list = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
		List<List<Object>> traineeDetails = this.filterTraineeDetails(batch);
		if (traineeDetails != null && !list.toString().contains("#NUM!")) {
			List<TraineeDto> traineData = traineeDetails.stream().map(wrapper::listToDto).collect(Collectors.toList());
			List<AttendanceDto> collect = list.stream().filter(items -> items.get(3).toString().equalsIgnoreCase(batch))
					.map(wrapper::attendanceListToDto).collect(Collectors.toList());
			collect.stream().forEach(item -> {
				Boolean checkAbsentDate = this.checkAbsentDate(item);
				if (checkAbsentDate == false) {
					traineData.stream().filter(dto -> item.getId().equals(dto.getId())).forEach(dto -> {
						traineeInfoList.add(new AttendanceTrainee(dto.getId(), dto.getBasicInfo().getTraineeName(),
								dto.getBasicInfo().getEmail()));
					});
				}
			});

			return traineeInfoList;
		}

		return traineeInfoList;

	}

	@Override
	public List<AbsentDaysDto> getAttendanceById(Integer id) {
		log.info("Searching for attendance with ID: {}", id);
		List<List<Object>> list;

		List<AbsentDaysDto> absentDaysList = new ArrayList<AbsentDaysDto>();

		list = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
		if (list != null) {
			log.debug("List in service: {}", list);
			List<AttendanceDto> matchingAttendances = list.stream().map(wrapper::attendanceListToDto)
					.filter(dto -> id.equals(dto.getId())).collect(Collectors.toList());

			if (!matchingAttendances.isEmpty()) {
				AttendanceDto attendanceListToDto = matchingAttendances.get(0);
				String[] splitAbsentDate = attendanceListToDto.getAbsentDate().split(",");
				String[] splitReason = attendanceListToDto.getReason().split(",");
				for (int i = 0; i < splitAbsentDate.length; i++) {
					AbsentDaysDto daysDto = new AbsentDaysDto();
					String date = splitAbsentDate[i].trim();
					String reason = splitReason[i].trim();
					daysDto.setDate(date);
					daysDto.setReason(reason);
					absentDaysList.add(daysDto);
				}

				return absentDaysList;
			}
		}

		log.warn("ID not found: {}", id);
		return null;

	}

	@Override
	public List<AttendanceDto> getAbsentListByBatch(String batch) {
		List<List<Object>> attendanceList;

		attendanceList = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
		if (attendanceList != null) {
			log.debug("attendanceList in service: {}", attendanceList);
			List<AttendanceDto> matchingAttendances = attendanceList.stream().map(wrapper::attendanceListToDto)
					.filter(dto -> batch.equals(dto.getCourse())).collect(Collectors.toList());
			if (!matchingAttendances.isEmpty()) {
				return matchingAttendances;
			}
		}
		return null;

	}

	@Override
	public Boolean markTrainerAttendance(String courseName, Boolean batchAttendanceStatus) {
		if (courseName != null && !batchAttendanceStatus.equals(false)) {
			BatchDetailsDto batchDetailsByCourseName;
			try {
				batchDetailsByCourseName = batchService.getBatchDetailsByCourseName(sheetId, courseName);

				if (batchDetailsByCourseName != null) {
					Boolean presentDate = batchAttendanceService.getPresentDate(courseName);
					if (presentDate == false) {
						BatchAttendanceDto setBatchValues = batchWrapper.setBatchValues(batchDetailsByCourseName);
						batchAttendanceService.writeBatchAttendance(setBatchValues);
						BatchDetailsDto detailsDto = new BatchDetailsDto();
						Integer totalclass = batchService.gettotalClassByCourseName(courseName);
						Integer batchAttendance = totalclass + 1;
						detailsDto.setTotalClass(batchAttendance);
						detailsDto.setCourseName(courseName);
						batchService.updateBatchDetails(courseName, detailsDto);
						return true;
					}

				} else {
					return false;
				}
			} catch (IOException | IllegalAccessException e) {
				log.error("Error marking trainee attendance: {}", e.getMessage());
			}
		}
		return false;
	}

	private List<List<Object>> filterTraineeDetails(String courseName) {
		List<List<Object>> followUpDetails = repository.getFollowUpDetails(sheetId);
		List<List<Object>> readData = registerRepository.readData(sheetId);
		List<List<Object>> traineeDetails = followUpDetails.stream().filter(followUpDetail -> {
			return Status.Joined.toString().equalsIgnoreCase(followUpDetail.get(8).toString())
					&& followUpDetail.get(15).toString().equalsIgnoreCase(ServiceConstant.ACTIVE.toString());
		}).map(followUpDetail -> {
			return readData.stream()
					.filter(data -> data.get(2).toString().equalsIgnoreCase(followUpDetail.get(2).toString()))
					.findFirst().orElse(null);
		}).collect(Collectors.toList());
		List<List<Object>> collect = traineeDetails.stream()
				.filter(dtos -> dtos != null && dtos.get(9).toString().equalsIgnoreCase(courseName))
				.collect(Collectors.toList());

		return collect;

	}

	@Override
	public List<AttendanceDto> addJoined(String courseName) {
		List<List<Object>> followUpDetails;
		followUpDetails = repository.getFollowUpDetails(sheetId);
		List<List<Object>> readData = registerRepository.readData(sheetId);
		List<AttendanceDto> attendanceDto = new ArrayList<AttendanceDto>();

		if (followUpDetails != null && !followUpDetails.isEmpty()) {
			List<FollowUpDto> optionalFollowupDto = followUpDetails(followUpDetails);
			List<TraineeDto> filterTraineDetails = filterTraineDetails(courseName, readData);
			if (!optionalFollowupDto.isEmpty()) {
				optionalFollowupDto.stream().forEach(followUpDto -> {
					filterTraineDetails.stream().filter(dto -> dto.getBasicInfo().getEmail()
							.equalsIgnoreCase(followUpDto.getBasicInfo().getEmail())).forEach(dto -> {
								try {
									AttendanceDto saveAttendance = wrapper.saveAttendance(dto);
									log.info("Created AttendanceDto: {}", saveAttendance);

									if (!this.traineeAlreadyAdded(courseName, saveAttendance.getId())) {
										processAttendance(saveAttendance, courseName);
										attendanceDto.add(saveAttendance);
									}

								} catch (IllegalAccessException | IOException e) {
									log.error("Error adding joined trainee: {}", e.getMessage());
								}

							});
				});
				return attendanceDto;
			}
		}
		return Collections.emptyList();

	}

	private void processAttendance(AttendanceDto saveAttendance, String courseName)
			throws IOException, IllegalAccessException {
		List<Object> extractDtoDetails = wrapper.extractDtoDetails(saveAttendance);
		extractDtoDetails.remove(4);
		boolean writeAttendance = attendanceRepository.writeAttendance(sheetId, extractDtoDetails, attendanceInfoRange);
		cacheService.addAttendancdeToCache("attendanceData", "listOfAttendance", extractDtoDetails);
		if (writeAttendance == true) {
			log.info("Attendance Details Updated successfully : {} ", sheetId);
		} else {
			log.debug("Attendance Details is not Added");
		}

	}

	@Override
	public ResponseEntity<AttendanceDataDto> attendanceReadData(Integer startingIndex, Integer maxRows,
			String courseName) {
		List<List<Object>> traineeDetails = filterTraineeDetails(courseName);
		List<AttendanceViewDto> viewDtos = new ArrayList<AttendanceViewDto>();
		List<List<Object>> attendanceData = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);

		List<AttendanceDto> attendance = attendanceData.stream().map(wrapper::attendanceListToDto)
				.collect(Collectors.toList());
		List<TraineeDto> filterTraineDetails = filterTraineDetails(courseName, traineeDetails);
		if (attendanceData != null) {
			if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				filterTraineDetails.stream().forEach(traineDtos -> {
					attendance.stream().filter(dtos -> dtos.getId().equals(traineDtos.getId())).forEach(items -> {
						AttendanceViewDto viewDto = new AttendanceViewDto();
						viewDto.setId(traineDtos.getId());
						viewDto.setName(traineDtos.getBasicInfo().getTraineeName());
						viewDto.setEmail(traineDtos.getBasicInfo().getEmail());
						viewDto.setCourseName(traineDtos.getCourseInfo().getCourse());
						viewDto.setTotalAbsent(items.getTotalAbsent());
						viewDtos.add(viewDto);
					});

				});

				List<AttendanceViewDto> limitedRows = viewDtos.stream()
						.sorted(Comparator.comparing(AttendanceViewDto::getName)).skip(startingIndex).limit(maxRows)
						.collect(Collectors.toList());
				AttendanceDataDto dto = new AttendanceDataDto(limitedRows, viewDtos.size());
				log.info("Returning response for spreadsheetId: {}", sheetId);
				return ResponseEntity.ok(dto);
			}
		}
		return null;

	}

	@Override
	public List<AttendanceViewDto> filterData(String searchValue, String courseName) {
		List<List<Object>> traineeDetails = filterTraineeDetails(courseName);
		List<AttendanceViewDto> viewDtos = new ArrayList<AttendanceViewDto>();
		List<List<Object>> attendanceData = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
		List<AttendanceDto> attendance = attendanceData.stream().map(wrapper::attendanceListToDto)
				.collect(Collectors.toList());
		List<TraineeDto> filterTraineDetails = filterTraineDetails(courseName, traineeDetails);
		if (searchValue != null && !searchValue.isEmpty()) {
			filterTraineDetails.stream().forEach(traineDtos -> {
				attendance.stream().filter(dtos -> dtos.getId().equals(traineDtos.getId())).forEach(items -> {
					AttendanceViewDto viewDto = new AttendanceViewDto();
					viewDto.setId(traineDtos.getId());
					viewDto.setName(traineDtos.getBasicInfo().getTraineeName());
					viewDto.setEmail(traineDtos.getBasicInfo().getEmail());
					viewDto.setCourseName(traineDtos.getCourseInfo().getCourse());
					viewDto.setTotalAbsent(items.getTotalAbsent());
					viewDtos.add(viewDto);
				});

			});
			if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				List<AttendanceViewDto> collect = viewDtos.stream().filter(
						value -> value != null && value.toString().toLowerCase().contains(searchValue.toLowerCase()))
						.filter(dto -> dto.getCourseName().equalsIgnoreCase(courseName)).collect(Collectors.toList());
				return collect;
			}
		} else {
			log.warn("Search value is null or empty. Returning an empty list.");
			return new ArrayList<>();
		}
		return viewDtos;

	}

	@Override
	public ResponseEntity<List<AttendanceViewDto>> getSearchSuggestion(String value, String courseName) {
		List<List<Object>> traineeDetails = filterTraineeDetails(courseName);
		List<AttendanceViewDto> viewDtos = new ArrayList<AttendanceViewDto>();
		List<List<Object>> attendanceData = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
		List<AttendanceDto> attendance = attendanceData.stream().map(wrapper::attendanceListToDto)
				.collect(Collectors.toList());
		List<TraineeDto> filterTraineDetails = filterTraineDetails(courseName, traineeDetails);
		if (value != null && !value.isEmpty()) {
			filterTraineDetails.stream().forEach(traineDtos -> {
				attendance.stream().filter(dtos -> dtos.getId().equals(traineDtos.getId())).forEach(items -> {
					AttendanceViewDto viewDto = new AttendanceViewDto();
					viewDto.setId(traineDtos.getId());
					viewDto.setName(traineDtos.getBasicInfo().getTraineeName());
					viewDto.setEmail(traineDtos.getBasicInfo().getEmail());
					viewDto.setCourseName(traineDtos.getCourseInfo().getCourse());
					viewDto.setTotalAbsent(items.getTotalAbsent());
					viewDtos.add(viewDto);
				});
			});

			if (!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
				List<AttendanceViewDto> viewDto = viewDtos.stream()
						.filter(dto -> dto.getCourseName().equalsIgnoreCase(courseName))
						.filter(dto -> dto.getName().toLowerCase().startsWith(value.toLowerCase()))
						.collect(Collectors.toList());
				return ResponseEntity.ok(viewDto);

			}

		}
		log.warn("Null value provided for search suggestion");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());

	}

	private List<FollowUpDto> followUpDetails(List<List<Object>> followUpDetails) {
		return followUpDetails.stream().filter(row -> Status.Joined.toString().equals(row.get(8)))
				.map(wrapper::listToFollowUpDTO).collect(Collectors.toList());
	}

	private List<TraineeDto> filterTraineDetails(String courseName, List<List<Object>> traineDetails) {
		return traineDetails.stream().filter(row -> courseName.equals(row.get(9))).map(wrapper::listToDto)
				.collect(Collectors.toList());
	}

	private void sendAbsentMail(Integer id, String courseName, String reason) {
		List<List<Object>> traineeDetails = this.filterTraineeDetails(courseName);
		List<TraineeDto> collect = traineeDetails.stream().map(wrapper::listToDto).collect(Collectors.toList());
		collect.stream().filter(dto -> dto.getId().equals(id)).forEach(dto -> {
			dreamUtil.sendAbsentMail(dto.getBasicInfo().getEmail(), dto.getBasicInfo().getTraineeName(), reason);
		});
	}

	private boolean checkContinuousAbsence(List<List<Object>> attendanceData, Integer traineeId) {
		LocalDate today = LocalDate.now();
		List<AttendanceDto> collect = attendanceData.stream().map(wrapper::attendanceListToDto)
				.collect(Collectors.toList());

		for (AttendanceDto attendanceDto : collect) {
			if (attendanceDto.getId().equals(traineeId)) {
				String absentDate = attendanceDto.getAbsentDate();
				String[] dates = absentDate.split(",");
				int consecutiveAbsentDays = 0;

				for (String date : dates) {
					if (date.equals(today.minusDays(1).toString()) || date.equals(today.minusDays(2).toString())
							|| date.equals(today.toString())) {
						consecutiveAbsentDays++;
					} else {
						consecutiveAbsentDays = 0;
					}

				}
				if (consecutiveAbsentDays >= 3) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void processAttendanceData() {
	    List<List<Object>> attendanceData = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
	    List<List<Object>> readData = registerRepository.readData(sheetId);

	    List<TraineeDto> listToDto = new ArrayList<>(); 
	    readData.forEach(dtos -> {
	        attendanceData.forEach(record -> {
	            Integer traineeId = Integer.valueOf(record.get(1).toString());
	            if (dtos.get(0).toString().equals(traineeId.toString())) {
	                boolean checkContinuousAbsence = checkContinuousAbsence(attendanceData, traineeId);
	                if (checkContinuousAbsence) {
	                    TraineeDto traineeDto = wrapper.listToDto(dtos); 
	                    listToDto.add(traineeDto);
	                }
	            }
	        });
	    });
	    dreamUtil.sendEmailNotificationForAttendanceFollowUp(listToDto);
	}


}
