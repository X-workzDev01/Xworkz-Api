package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AttadanceSheetDto;
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
	@Value("${sheets.attendanceInfoByName}")
	private String attendanceInfoByName;
	@Value("${sheets.attendanceListRange}")
	private String attendanceListRange;
	@Value("${sheets.rowStartRange}")
	private String rowStartRange;
	@Value("${sheets.attendanceList}")
	private String attendanceList;
	@Value("${sheets.absentRange}")
	private String absentRange;
	@Value("${sheets.attendanceListDefaultRange}")
	private String attendanceListDefaultRange;
	@Value("${sheets.presentRange}")
	private String presentRange;
	@Value("${sheets.AttandanceInfoSheetName}")
	private String AttandanceInfoSheetName;
	private AttendanceDto attendanceDto;
	@Value("${sheets.attendanceInfoMarkAs}")
	private String attendanceMarkAs;
	private int present = 0;
	private int absent = 0;

	@Autowired
	private DreamWrapper wrapper;

	Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

	@Override
	public ResponseEntity<String> writeAttendance(String spreadsheetId, AttendanceDto dto, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException {

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

		Set<ConstraintViolation<AttendanceDto>> violation = factory.getValidator().validate(dto);

		if (violation.isEmpty() && dto != null) {
			if (dto.getAttemptStatus().equalsIgnoreCase(Status.Joined.toString())) {
				dto.getCourseInfo().setStartTime(LocalDateTime.now().toString());
				List<Object> list = wrapper.listOfAttendance(dto);
				attendanceRepository.writeAttendance(spreadsheetId, list, attendanceInfoRange);
				logger.info("Attendance Detiles Added Sucessfully SpreadsheetId: {} , Detiles: {} ", spreadsheetId,
						dto.toString());

				return ResponseEntity.ok("Attendance Detiles Added Sucessfully");

			}

		}

		return ResponseEntity.ok("Attendance detiles does not added ");

	}

	@Override
	public ResponseEntity<String> everyDayAttendance(AttendanceDto dto, HttpServletRequest request) throws Exception {

		if (dto != null) {

			List<Object> list = wrapper.listOfAddAttendance(dto);

			boolean writeStatus = attendanceRepository.everyDayAttendance(sheetId, list, attendanceListRange);
			if (writeStatus == true) {

				List<List<Object>> attendanceList = attendanceRepository.attendanceDetilesByEmail(sheetId,
						dto.getBasicInfo().getEmail(), attendanceListRange);
				present = 0;
				absent = 0;
				int rowIndex = findRowIndexByid(sheetId, dto.getBasicInfo().getEmail());
				String presentrange = AttandanceInfoSheetName + presentRange + rowIndex;
				String updateRange = AttandanceInfoSheetName + attendanceMarkAs + rowIndex;

				if (dto.getMarkAs().equals("1")) {
					ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList("true", "blue", "gray")));
					System.err.println(body);

					attendanceRepository.update(sheetId, updateRange, body);
				}
				if (dto.getMarkAs().equalsIgnoreCase("0")) {
					ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList("true", "gray", "blue")));

					attendanceRepository.update(sheetId, updateRange, body);
				}
				if (attendanceList != null) {
					logger.debug("Every day attendance detiles added sucessfully {}", attendanceList);
					System.err.println(attendanceList);

					attendanceList.stream().forEach(f -> {
						attendanceDto = wrapper.attendanceListToDto(f);
						if (attendanceDto.getMarkAs() != null) {
							if (attendanceDto.getBasicInfo().getEmail().equalsIgnoreCase(dto.getBasicInfo().getEmail())
									&& attendanceDto.getMarkAs().equals("1")) {

								present++;

							}
						}
						if (attendanceDto.getMarkAs() != null) {

							if (attendanceDto.getBasicInfo().getEmail().equalsIgnoreCase(dto.getBasicInfo().getEmail())
									&& attendanceDto.getMarkAs().equals("0")) {
								absent++;
							}
						}

					});
				}

				ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList(present, absent)));
				attendanceRepository.update(sheetId, presentrange, body);

			}

		}
		logger.info("Attendance Detiles Added Sucessfully SpreadsheetId: {} , Detiles: {} ", sheetId, dto);
		return ResponseEntity.ok("Total Present is " + present + "ABsent is " + absent);

	}

	private int findRowIndexByid(String spreadsheetId, String email) throws IOException {

		List<List<Object>> data = attendanceRepository.getEmail(spreadsheetId, attendanceInfoIDRange);
		if (data != null) {
			if (data != null && !data.isEmpty()) {
				for (int i = 0; i < data.size(); i++) {
					List<Object> row = data.get(i);
					if (row.size() > 0 && row.get(2).toString().equalsIgnoreCase(email)) {
						return i + 2;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public ResponseEntity<AttadanceSheetDto> getAttendanceDetilesByEmail(String email, int startIndex, int maxRows)
			throws IOException, MessagingException, TemplateException {

		List<List<Object>> attandanceList = attendanceRepository.attendanceDetilesByEmail(sheetId, email,
				attendanceListRange);
		if (attandanceList != null) {
			List<List<Object>> filter = attandanceList.stream().filter(e -> e.contains(email))
					.collect(Collectors.toList());

			List<AttendanceDto> dtos = this.getLimitedRowsByEmail(filter, email, startIndex, maxRows);

			AttadanceSheetDto dtosList = new AttadanceSheetDto(dtos, filter.size());
			logger.debug("Response attandance by email {}", dtosList);

			return ResponseEntity.ok(dtosList);
		}
		return null;
	}

	@Override
	public ResponseEntity<AttadanceSheetDto> getAttendanceDetilesBatch(String batch, int startIndex, int maxRows)
			throws IOException, MessagingException, TemplateException {

		List<List<Object>> attendanceList = attendanceRepository.attendanceDetilesByEmail(sheetId, batch,
				attendanceInfoRange);

		if (attendanceList != null) {
			List<List<Object>> filter = attendanceList.stream().filter(e -> e.contains(batch))
					.collect(Collectors.toList());
			System.err.println("ffffffffffffffffffffffffffffffff" + filter);
			

			List<AttendanceDto> dtos = this.getLimitedRows(filter, startIndex, maxRows);

			AttadanceSheetDto sheetDto = new AttadanceSheetDto(dtos, filter.size());
			logger.debug("Dto is  attandance :{} ", sheetDto);
			System.err.println("ffffffffffffffffffffffffffffffff" + sheetDto);

			return ResponseEntity.ok(sheetDto);
		}
		return null;
	}

	private List<AttendanceDto> getLimitedRowsByEmail(List<List<Object>> values, String email, int startingIndex,
			int maxRows) {
		List<AttendanceDto> attendanceDtos = new ArrayList<>();

		int endIndex = startingIndex + maxRows;

		ListIterator<List<Object>> iterator = values.listIterator(startingIndex);

		while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
			List<Object> row = iterator.next();

			if (row != null && !row.isEmpty()) {
				AttendanceDto attendanceDto = wrapper.attendanceListToDto(row);
				if (attendanceDto.getBasicInfo().getEmail().equalsIgnoreCase(email)) {
					attendanceDtos.add(attendanceDto);
				}
			}
		}
		return attendanceDtos;
	}

	private List<AttendanceDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows) {
		List<AttendanceDto> attendanceDtos = new ArrayList<>();

		int endIndex = startingIndex + maxRows;
		//int rowCount = values.size();

		ListIterator<List<Object>> iterator = values.listIterator(startingIndex);

		while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
			List<Object> row = iterator.next();

			if (row != null && !row.isEmpty()) {
				AttendanceDto attendanceDto = wrapper.attendanceListEverydayToDto(row);

				attendanceDtos.add(attendanceDto);
			}
		}
		return attendanceDtos;
	}

	private List<AttendanceDto> getLimitedRowsBatchAndDate(List<List<Object>> values, String batch, String date,
			int startingIndex, int maxRows) {
		List<AttendanceDto> attendanceDtos = new ArrayList<>();

		int endIndex = startingIndex + maxRows;

		ListIterator<List<Object>> iterator = values.listIterator(startingIndex);

		while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
			List<Object> row = iterator.next();

			if (row != null && !row.isEmpty()) {
				AttendanceDto attendanceDto = wrapper.attendanceListToDto(row);
				if (attendanceDto.getCourseInfo().getCourse().equals(batch) && attendanceDto.getDate().equals(date)) {
					attendanceDtos.add(attendanceDto);
				}
			}
		}
		return attendanceDtos;
	}

	@Override
	public ResponseEntity<AttadanceSheetDto> getAttendanceDetilesBatchAndDate(String batch, String date, int startIndex,
			int maxRows) throws IOException, MessagingException, TemplateException {
		List<List<Object>> attandanceList = attendanceRepository.attendanceDetilesByEmail(sheetId, batch,
				attendanceList);
		if (attandanceList != null) {
			List<List<Object>> filter = attandanceList.stream().filter(e -> e.contains(batch) && e.contains(date))
					.collect(Collectors.toList());
			List<AttendanceDto> dtos = getLimitedRowsBatchAndDate(filter, batch, date, startIndex, maxRows);
			AttadanceSheetDto attandanceSheetDto = new AttadanceSheetDto(dtos, filter.size());
			logger.debug("Get attandance detiles by date is {} ", dtos);
			return ResponseEntity.ok(attandanceSheetDto);
		}
		return null;
	}

	@Scheduled(fixedRate = 60 * 24 * 60 * 1000) // 1000 is equal to 1 second
	public void schudulerAttandance() throws IOException {

		attendanceRepository.clearColumnData(sheetId, "attendanceInfo!J2:J");
		attendanceRepository.clearColumnData(sheetId, "attendanceInfo!K2:k");
		attendanceRepository.clearColumnData(sheetId, "attendanceInfo!L2:L");

	}

}
