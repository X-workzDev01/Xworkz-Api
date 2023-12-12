package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.xworkz.dream.dto.AttendanceSheetDto;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.service.AttendanceService;


import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;
@RestController
@RequestMapping("/api")
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	@Value("${login.sheetId}")
	private String spreadsheetId;
	Logger logger = LoggerFactory.getLogger(AttendanceController.class);

	@ApiOperation(value = "To register attendance details in the google sheets")
	@PostMapping("/registerAttendance")
	public ResponseEntity<String> registerAttendance(@RequestBody AttendanceDto values, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException {
		System.err.println("values in controller : "+values);
		logger.debug("Registering trainee details: {}", values);
		logger.debug("Attendance Register sucessfully {} ", values);
		return attendanceService.writeAttendance(spreadsheetId, values, request);
	}
	
	@ApiOperation(value = "Everyday mark attendance")
	@PostMapping("/addAttendennce")
	public void markAttendance(@RequestBody List<AttendanceDto> attendanceDtoList) throws IOException, IllegalAccessException {
		System.err.println("attendanceDtoList : "+attendanceDtoList);
		attendanceService.markAndSaveAbsentDetails(attendanceDtoList);
		
	}
	

//	@PostMapping("/addAttendennce")
//	@ApiOperation(value = "Everyday day Add attendance Entry")
//
//	public ResponseEntity<String> everyDayAttendance(@RequestBody AttendanceDto dto, HttpServletRequest request)
//			throws Exception {
//		return attendanceService.everyDayAttendance(dto, request);
//	}
//
//	@ApiOperation(value = "Get detiles of by using email")
//	@GetMapping("/byEmail")
//	public ResponseEntity<AttendanceSheetDto> getAttendanceListByEmail(@RequestParam String email,
//			@RequestParam int startIndex, @RequestParam int maxRows) throws Exception {
//		return attendanceService.getAttendanceDetilesByEmail(email, startIndex, maxRows);
//	}
//
//	@ApiOperation(value = "Get detiles in using selected  batch ")
//	@GetMapping("/byBatch")
//	public ResponseEntity<AttendanceSheetDto> getAttendanceListByBatch(@RequestParam String batch,
//			@RequestParam int startIndex, @RequestParam int maxRows) throws Exception {
//
//		return attendanceService.getAttendanceDetilesBatch(batch, startIndex, maxRows);
//
//	}
//
//	@ApiOperation(value = "Get detiles in using selected  batch  and date")
//	@GetMapping("/byBatchAndDate")
//	public ResponseEntity<AttendanceSheetDto> getAttendanceListByBatchAndDate(@RequestParam String batch,
//			@RequestParam String date, @RequestParam int startIndex, @RequestParam int maxRows) throws Exception {
//		return attendanceService.getAttendanceDetilesBatchAndDate(batch, date, startIndex, maxRows);
//
//	}

	

}
