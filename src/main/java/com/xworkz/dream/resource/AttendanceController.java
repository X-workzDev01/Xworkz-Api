package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repo.DreamRepo;
import com.xworkz.dream.service.AttendanceService;

import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
	@Autowired
	private DreamRepo repo;
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

	@ApiOperation(value = "To register the add new colomn in the google sheets")
	@PutMapping("/addColumnToAttendance")
	public ResponseEntity<String> addcolumn(@RequestHeader String spreadsheetId, HttpServletRequest request)
			throws Exception {
		attendanceService.addcolumn(spreadsheetId, request);
		return null;

	}

//	@PutMapping("/update")
//	public ResponseEntity<String> updateValueById(@RequestParam String id, @RequestHeader String spreadsheetId,
//			@RequestParam String newValue) throws IOException {
//
//		repo.updateValueById(id, spreadsheetId, newValue);
//
//		return null;
//
//	}
}
