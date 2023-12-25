package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.repository.AttendanceRepository;
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
	@Value("${sheets.AttandanceInfoSheetName}")
	private String AttandanceInfoSheetName;

	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private CacheService cacheService;

	private static Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);

	@Override
	public ResponseEntity<String> writeAttendance(String spreadsheetId, AttendanceDto dto, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

		Set<ConstraintViolation<AttendanceDto>> violation = factory.getValidator().validate(dto);

		if (violation.isEmpty() && dto != null) {
			if (dto.getAttemptStatus().equalsIgnoreCase(Status.Joined.toString())) {
				dto.getCourseInfo().setStartDate(LocalDateTime.now().toString());
				wrapper.setValueAttendaceDto(dto);
				List<Object> list = wrapper.listOfAttendance(dto);
				attendanceRepository.writeAttendance(spreadsheetId, list, attendanceInfoRange);
				log.info("Attendance Detiles Added Sucessfully SpreadsheetId: {} , Detiles: {} ", spreadsheetId,
						dto.toString());

				return ResponseEntity.ok("Attendance Detiles Added Sucessfully");

			}

		}

		return ResponseEntity.ok("Attendance detiles does not added ");

	}

	@Override
	public void markAndSaveAbsentDetails(List<AttendanceDto> attendanceDtoList)
			throws IOException, IllegalAccessException {
		log.info("Marking and saving absent details...");
		List<List<Object>> attendanceList = attendanceRepository.getId(sheetId, attendanceInfoIDRange);
		log.info("attendanceList in attendanceinfo sheet: " + attendanceList);

		for (List<Object> list : attendanceList) {
			AttendanceDto attendanceDto = wrapper.attendanceListToDto(list);
			System.err.println("toDto: " + attendanceDto);
			{
				for (AttendanceDto dto : attendanceDtoList) 
					updateAttendanceDetails(attendanceDto, dto);

				}
			
		}
		
	}

	private void updateAttendanceDetails(AttendanceDto attendanceDto, AttendanceDto dto)
			throws IOException, IllegalAccessException {
		if (dto.getId().equals(attendanceDto.getId())) {
			int rowIndex = findByID(sheetId, attendanceDto.getId());
			String range = AttandanceInfoSheetName + attendanceStartRange + rowIndex + ":" + attendanceEndRange
					+ rowIndex;
			System.err.println("range : " + range);
			if (attendanceDto.getId() != null) {
				updateAbsentDatesAndReasons(dto, attendanceDto);
				updateTotalAbsent(dto, attendanceDto);
				List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(dto));
				ValueRange valueRange = new ValueRange();
				valueRange.setValues(values);
				System.err.println(" values :" + values);
				UpdateValuesResponse update = attendanceRepository.update(sheetId, range, valueRange);
				cacheService.updateCacheAttendancde("attendanceDataGetById", sheetId, dto.getId(), dto);
				if (update.isEmpty()) {
					log.info("Not updated attendance");
				} else {
					log.info("Attendance updated successfully");
				}
			}
		}
	}

	private void updateAbsentDatesAndReasons(AttendanceDto dto, AttendanceDto attendanceDto) {
		dto.setBasicInfo(attendanceDto.getBasicInfo());
		dto.setCourseInfo(attendanceDto.getCourseInfo());
		String currentAbsentDates = attendanceDto.getAbsentDate();
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String newAbsentDate = localDate.format(formatter);
		String updatedAbsentDates = !currentAbsentDates.equals(null) && currentAbsentDates.contains("NA")
				? newAbsentDate
				: currentAbsentDates + "," + newAbsentDate;
		dto.setAbsentDate(updatedAbsentDates);

		String currentReason = attendanceDto.getReason();
		String newReason = dto.getReason();
		String updatedReasons = !currentReason.equals(null) && currentReason.contains("NA") ? newReason
				: currentReason + "," + newReason;
		dto.setReason(updatedReasons);
		log.info("Absent dates and reasons updated: " + dto.getAbsentDate() + ", " + dto.getReason());
	}

	private void updateTotalAbsent(AttendanceDto dto, AttendanceDto attendanceDto) {
		Integer currentAbsent = attendanceDto.getTotalAbsent();
		Integer updateAbsent = !currentAbsent.equals(null) && currentAbsent.equals(0) ? 1 : currentAbsent + 1;
		dto.setTotalAbsent(updateAbsent);
		log.info("Total absent updated: " + dto.getTotalAbsent());
	}

	private int findByID(String spreadsheetId, Integer id) throws IOException {

		List<List<Object>> data = attendanceRepository.getId(spreadsheetId, attendanceInfoIDRange);
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

}
