package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.repository.AttendanceRepository;
import com.xworkz.dream.wrapper.DreamWrapper;

import freemarker.template.TemplateException;

@Service
public class AttendanceServiceImpl implements AttendanceService{
	@Autowired
	private AttendanceRepository repo;
	@Value("${login.sheetId}")
	private String sheetId;
	private AttendanceDto attendanceDto;

	@Autowired
	private DreamWrapper wrapper;

	Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

	@Override
	public ResponseEntity<String> writeAttendance(@RequestHeader String spreadsheetId, @RequestBody AttendanceDto dto,
			HttpServletRequest request) throws IOException, MessagingException, TemplateException {

		try {
			if (true && dto.getAttemptStatus().equalsIgnoreCase(Status.Joined.toString())) {// isCookieValid(request)
				dto.getCourseInfo().setStartTime(LocalDateTime.now().toString());
				List<Object> list = wrapper.listOfAttendance(dto);
				boolean writeStatus = repo.writeAttendance(spreadsheetId, list);
				return null;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@Override
	public ResponseEntity<String> everyDayAttendance(AttendanceDto dto, HttpServletRequest request) throws Exception {
		int present = 0;
		int absent = 0;
		if (dto != null) {
			List<Object> list = wrapper.listOfAddAttendance(dto);
			System.err.println("Listrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" + list);
			boolean writeStatus = repo.everyDayAttendance(sheetId, list);
			if (writeStatus == true) {

				List<List<Object>> attendanceList = repo.attendanceDetilesByEmail(sheetId,
						dto.getBasicInfo().getEmail());
				attendanceList.stream().forEach(f -> attendanceDto = wrapper.attendanceListToDto(f));
				if (attendanceDto.getBasicInfo().getEmail().equalsIgnoreCase(dto.getBasicInfo().getEmail())) {

					if (dto.getMarkAs() == 1)
						present++;
					ResponseEntity.ok(present);

				}
				if (dto.getMarkAs() == 0) {
					absent++;
					ResponseEntity.ok(absent);
				}
				System.out.println(attendanceList);
			}
		}

		else {
			ResponseEntity.ok("Attendance detiles  has been null");
		}

		return null;

	}


}
