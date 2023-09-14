package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.repo.DreamRepo;
import com.xworkz.dream.resource.AttendanceController;
import com.xworkz.dream.wrapper.DreamWrapper;

import freemarker.template.TemplateException;

@Service
public class AttendanceService {
	@Autowired
	private DreamRepo repo;
	@Autowired
	private DreamWrapper wrapper;

	Logger logger = LoggerFactory.getLogger(AttendanceService.class);

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

	public ResponseEntity<String> addcolumn(@RequestHeader String spreadsheetId, HttpServletRequest request)
			throws Exception {
		
		
		if (LocalDate.now().isEqual(LocalDate.now())) {

			repo.addColumn(spreadsheetId);
		}
		return null;

	}

}
