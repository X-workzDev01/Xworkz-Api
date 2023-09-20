package com.xworkz.dream.resource;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.repository.DreamRepositoryImpl;
import com.xworkz.dream.service.AttendanceService;
import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
	@Autowired
	private DreamRepositoryImpl repo;
	@Autowired
	private AttendanceService attendanceService;
	Logger logger = LoggerFactory.getLogger(AttendanceController.class);

	@ApiOperation(value = "To register attendance details in the google sheets")
	@PostMapping("/registerAttendance")
	public ResponseEntity<String> registerAttendance(@RequestHeader String spreadsheetId,
			@RequestBody AttendanceDto values, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException {
		logger.info("Registering trainee details: {}", values);

		attendanceService.writeAttendance(spreadsheetId, values, request);

		return null;

	}

	@PostMapping("/addAttendennce")
	public ResponseEntity<String> everyDayAttendance(@RequestBody AttendanceDto dto, HttpServletRequest request)
			throws Exception {
		return attendanceService.everyDayAttendance(dto, request);
	}

}
