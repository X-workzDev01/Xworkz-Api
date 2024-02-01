package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AbsentDaysDto;
import com.xworkz.dream.dto.AbsenteesDto;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.AttendanceTrainee;
import com.xworkz.dream.dto.BatchAttendanceDto;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.utils.WrapperUtil;
import com.xworkz.dream.repository.AttendanceRepository;
import com.xworkz.dream.wrapper.BatchWrapper;
import com.xworkz.dream.wrapper.DreamWrapper;

import freemarker.template.TemplateException;

@Service
public class AttendanceServiceImpl implements AttendanceService {
	@Autowired
	private AttendanceRepository attendanceRepository;
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
	@Value("${sheets.batchAttendanceInfoRange}")
	private String batchAttendanceInfoRange;
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

	private static Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);

	@Override
	public ResponseEntity<String> writeAttendance(String spreadsheetId, AttendanceDto dto, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException, IllegalAccessException {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Set<ConstraintViolation<AttendanceDto>> violation = factory.getValidator().validate(dto);
		Boolean traineeAlreadyAdded = this.traineeAlreadyAdded(dto.getCourse(), dto.getId());
		if (traineeAlreadyAdded == false) {
			if (violation.isEmpty() && dto != null) {
				if (dto.getAttemptStatus().equalsIgnoreCase(Status.Joined.toString())) {
					wrapper.setValueAttendaceDto(dto);
					List<Object> list = util.extractDtoDetails(dto);
					list.remove(4);
					attendanceRepository.writeAttendance(spreadsheetId, list, attendanceInfoRange);
					cacheService.addAttendancdeToCache("attendanceData", "listOfAttendance", list);
					log.info("Attendance Detiles Added Sucessfully SpreadsheetId: {} , Detiles: {} ", spreadsheetId,
							dto.toString());

					return ResponseEntity.ok("Attendance Detiles Added Sucessfully");

				}

			}
		} else {
			return ResponseEntity.ok("Trainee Already Added To Attendance");
		}

		return ResponseEntity.ok("Attendance detiles does not added ");

	}

	@Override
	public Boolean traineeAlreadyAdded(String courseName, Integer id) throws IOException {
		List<AttendanceDto> absentListByBatch = this.getAbsentListByBatch(courseName);
		if(absentListByBatch !=null) {
		boolean anyMatch = absentListByBatch.stream().anyMatch(dto -> {
			if(dto.getId() !=null) {
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
	public void markAndSaveAbsentDetails(@RequestBody List<AbsenteesDto> absentDtoList, @RequestParam String batch)
			throws IOException, IllegalAccessException {
		log.info("Marking and saving absent details...");
		List<List<Object>> attendanceList = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
		log.info("attendanceList in attendanceinfo sheet: " + attendanceList);
		List<List<Object>> filteredList = attendanceList.stream().filter(entry -> batch.equals(entry.get(3)))
				.collect(Collectors.toList());

		for (List<Object> values : filteredList) {
			AttendanceDto attendanceDto = wrapper.attendanceListToDto(values);
			{
				for (AbsenteesDto dto : absentDtoList)
					updateAttendanceDetails(attendanceDto, dto);

			}

		}
	}

	private void updateAttendanceDetails(AttendanceDto attendanceDto, AbsenteesDto dto)
			throws IOException, IllegalAccessException {
		if (dto.getId().equals(attendanceDto.getId())) {
			int rowIndex = findByID(sheetId, dto.getId());
			String range = attandanceInfoSheetName + attendanceStartRange + rowIndex + ":" + attendanceEndRange
					+ rowIndex;

			if (attendanceDto.getId() != null) {
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
				if (update.isEmpty()) {
					log.info("Not updated attendance");
				} else {
					attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
					log.info("Attendance updated successfully");
				}
			}
		}
	}

	private void updateAbsentDatesAndReasons(AttendanceDto attendanceDto, AbsenteesDto dto) {
		String currentAbsentDates = attendanceDto.getAbsentDate();
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String newAbsentDate = localDate.format(formatter);
		String updatedAbsentDates = !currentAbsentDates.equals(null) && currentAbsentDates.contains("NA")
				? newAbsentDate
				: currentAbsentDates + "," + newAbsentDate;
		attendanceDto.setAbsentDate(updatedAbsentDates);

		String currentReason = attendanceDto.getReason();
		String newReason = dto.getReason();
		String updatedReasons = !currentReason.equals(null) && currentReason.contains("NA") ? newReason
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
		try {
			List<List<Object>> list = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
			List<List<Object>> filteredList = list.stream().filter(entry -> batch.equals(entry.get(3))) // Filter by
					.collect(Collectors.toList());

			List<AttendanceTrainee> traineeInfoList = new ArrayList<>();

			for (List<Object> entry : filteredList) {
				try {
					Integer id = Integer.valueOf(entry.get(1).toString());
					String name = (String) entry.get(2);
					traineeInfoList.add(new AttendanceTrainee(id, name));
				} catch (NumberFormatException e) {
					e.getMessage();
				}
			}
			return traineeInfoList;
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<AbsentDaysDto> getAttendanceById(Integer id) {
		log.info("Searching for attendance with ID: {}", id);
		List<List<Object>> list;

		List<AbsentDaysDto> absentDaysList = new ArrayList<AbsentDaysDto>();
		try {
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
		} catch (IOException e) {
			log.error("Error while fetching attendance data", e.getMessage());
		}
		log.warn("ID not found: {}", id);
		return null;

	}

	@Override
	public List<AttendanceDto> getAbsentListByBatch(String batch) throws IOException {
		List<List<Object>> attendanceList = attendanceRepository.getAttendanceData(sheetId, attendanceInfoIDRange);
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
	public Boolean markTraineeAttendance(String courseName, Boolean batchAttendanceStatus)
			throws IOException, IllegalAccessException {
		if (courseName != null && !batchAttendanceStatus.equals(false)) {
			BatchDetailsDto batchDetailsByCourseName = batchService.getBatchDetailsByCourseName(sheetId, courseName);
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

			}
		} else {
			return false;
		}
		return false;
	}

}
