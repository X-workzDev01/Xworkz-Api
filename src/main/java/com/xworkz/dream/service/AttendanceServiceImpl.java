package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.repository.AttendanceRepository;
import com.xworkz.dream.wrapper.DreamWrapper;

import freemarker.template.TemplateException;
import lombok.val;

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
	@Value("${sheets.presentRange}")
	private String presentRange;
	@Value("${sheets.AttandanceInfoSheetName}")
	private String AttandanceInfoSheetName;
	private AttendanceDto attendanceDto;
	private int present = 0;
	private int absent = 0;

	@Autowired
	private DreamWrapper wrapper;

	Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

	@Override
	public ResponseEntity<String> writeAttendance(@RequestHeader String spreadsheetId, @RequestBody AttendanceDto dto,
			HttpServletRequest request) throws IOException, MessagingException, TemplateException {

		if (dto.getAttemptStatus().equalsIgnoreCase(Status.Joined.toString())) {
			dto.getCourseInfo().setStartTime(LocalDateTime.now().toString());
			List<Object> list = wrapper.listOfAttendance(dto);
			attendanceRepository.writeAttendance(spreadsheetId, list, attendanceInfoRange);
			return ResponseEntity.ok("Attendance Detiles Added Sucessfully");

		}
		
		return ResponseEntity.ok("Attendance detiles already exists");

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

				attendanceList.stream().forEach(f -> {
					attendanceDto = wrapper.attendanceListToDto(f);

					if (attendanceDto.getBasicInfo().getEmail().equalsIgnoreCase(dto.getBasicInfo().getEmail())
							&& attendanceDto.getMarkAs().equals("1")) {

						present++;

					}
					if (attendanceDto.getBasicInfo().getEmail().equalsIgnoreCase(dto.getBasicInfo().getEmail())
							&& attendanceDto.getMarkAs().equals("0")) {
						absent++;
					}

				});

				ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList(present, absent)));
				attendanceRepository.update(sheetId, presentrange, body);

			}

		}

		return ResponseEntity.ok("Total Present is " + present + "ABsent is " + absent);

	}

	private int findRowIndexByid(String spreadsheetId, String email) throws IOException {

		List<List<Object>> data = attendanceRepository.getEmail(spreadsheetId, attendanceInfoIDRange);
		if (data != null && !data.isEmpty()) {
			for (int i = 0; i < data.size(); i++) {
				List<Object> row = data.get(i);
				if (row.size() > 0 && row.get(2).toString().equalsIgnoreCase(email)) {
					return i + 2;
				}
			}
		}
		return -1;
	}

	@Override
	public ResponseEntity<List<AttendanceDto>> getAttendanceDetilesByEmail(String Email)
			throws IOException, MessagingException, TemplateException {
		List<AttendanceDto> dtos = new ArrayList<AttendanceDto>();
		List<List<Object>> attandanceList = attendanceRepository.attendanceDetilesByEmail(sheetId, Email,
				attendanceInfoRange);
		attandanceList.stream().forEach(f -> {
			AttendanceDto dto = wrapper.attendanceListToDto(f);
			dtos.add(dto);

		});

		return ResponseEntity.ok(dtos);
	}

	@Override
	public ResponseEntity<List<AttendanceDto>> getAttendanceDetilesBatch(String batch)
			throws IOException, MessagingException, TemplateException {
		List<AttendanceDto> dtos = new ArrayList<AttendanceDto>();
		List<List<Object>> attandanceList = attendanceRepository.attendanceDetilesByEmail(sheetId, batch,
				attendanceInfoRange);
		attandanceList.stream().forEach(f -> {
			if (!f.isEmpty() && f.toString() != null) {
				AttendanceDto dto = wrapper.attendanceListEverydayToDto(f);
				if (dto.getCourseInfo().getCourse().equals(batch)) {
					dtos.add(dto);
				}
			}
		});
		return ResponseEntity.ok(dtos);
	}

	@Override
	public ResponseEntity<List<AttendanceDto>> getAttendanceDetilesBatchAndDate(String batch, String date)
			throws IOException, MessagingException, TemplateException {
		List<AttendanceDto> dtos = new ArrayList<AttendanceDto>();
		List<List<Object>> attandanceList = attendanceRepository.attendanceDetilesByEmail(sheetId, batch,
				attendanceList);
		attandanceList.stream().forEach(f -> {
			AttendanceDto dto = wrapper.attendanceListEverydayToDto(f);
			if (dto.getCourseInfo().getCourse().equals(batch) && dto.getDate().equals(date)) {
				dtos.add(dto);
			}

		});
		return ResponseEntity.ok(dtos);
	}

}
