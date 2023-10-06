package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.BatchDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xworkz.dream.dto.BasicInfoDto;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.SuggestionDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.User;
import com.xworkz.dream.service.DreamService;

import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableScheduling
@RequestMapping("/api")
public class DreamApiController {

	Logger logger = LoggerFactory.getLogger(DreamApiController.class);

	private DreamService service;

	@Autowired
	public DreamApiController(DreamService service) {
		this.service = service;
	}

	@ApiOperation(value = "To register the trainee details in the google sheets")
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestHeader String spreadsheetId, @RequestBody TraineeDto values,
			HttpServletRequest request) throws IOException, MessagingException, TemplateException {
		logger.info("Registering trainee details: {}", values);

		return service.writeData(spreadsheetId, values, request);
	}

	@ApiOperation(value = "To register Check whether email already exist while registering")
	@GetMapping("/emailCheck")
	public ResponseEntity<String> emailCheck(@RequestHeader String spreadsheetId, @RequestParam String email,
			HttpServletRequest request) {
		logger.info("Checking email: {}", email);
		return service.emailCheck(spreadsheetId, email, request);
	}

	@ApiOperation(value = "To get Suggestions while search")
	@GetMapping("register/suggestion")
	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(@RequestHeader String spreadsheetId,
			@RequestParam String value, HttpServletRequest request) {
		logger.info("Getting suggesstions: {}", value);
		return service.getSearchSuggestion(spreadsheetId, value, request);

	}

	@ApiOperation(value = "To register Check whether contact number already exist while registering")
	@GetMapping("/contactNumberCheck")
	public ResponseEntity<String> contactNumberCheck(@RequestHeader String spreadsheetId,
			@RequestParam Long contactNumber, HttpServletRequest request) {
		logger.info("Checking contact number: {}", contactNumber);
		return service.contactNumberCheck(spreadsheetId, contactNumber, request);
	}

	@GetMapping("/readData")
	public ResponseEntity<SheetsDto> readData(@RequestHeader String spreadsheetId, @RequestParam int startingIndex,
			@RequestParam int maxRows) {
		return service.readData(spreadsheetId, startingIndex, maxRows);
	}

	@GetMapping("/filterData")
	public List<TraineeDto> filterData(@RequestHeader String spreadsheetId, @RequestParam String searchValue) {
		try {
			return service.filterData(spreadsheetId, searchValue);
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;

	}

	@ApiOperation(value = "To Update Registrated Details of Trainee")
	@PutMapping("/update")
	public ResponseEntity<String> update(@RequestHeader String spreadsheetId, @RequestParam String email,
			@RequestBody TraineeDto dto) {
		return service.update(spreadsheetId, email, dto);
	}

	@ApiOperation(value = "To Update the follow Up status using ID")
	@PostMapping("/updateFollowStatus")
	public ResponseEntity<String> updateFollowUpStatus(@RequestHeader String spreadsheetId,
			@RequestBody StatusDto statusDto, HttpServletRequest request) throws IOException {
		logger.info("updating follow up status : {}", statusDto);
		return service.updateFollowUpStatus(spreadsheetId, statusDto, request);
	}

//	public <ResponseEntity<FollowUpDto>> getFollowUpData(@RequestHeader String spreadsheetId , star)

	// suhas
	@GetMapping("/getCourseName")
	public ResponseEntity<List<Object>> getCourseName(@RequestHeader String spreadsheetId, @RequestParam String status)
			throws IOException {
		logger.info("Getting CourseName : {}", status);
		return service.getCourseNameByStatus(spreadsheetId, status);
	}

	// suhas
	@GetMapping("/getCourseDetails")
	public ResponseEntity<BatchDetails> getBatchDetails(@RequestHeader String spreadsheetId,
			@RequestParam String courseName) throws IOException {
		logger.info("Getting CourseDetails : {}", courseName);
		return service.getBatchDetailsByCourseName(spreadsheetId, courseName);

	}

	@ApiOperation(value = "To get Registration details by email")
	@GetMapping("/readByEmail")
	public ResponseEntity<?> getDataByEmail(@RequestHeader String spreadsheetId, @RequestParam String email,
			HttpServletRequest request) throws IOException {
		return service.getDetailsByEmail(spreadsheetId, email, request);
	}

	@ApiOperation(value = "To get follow up details by pagination")
	@GetMapping("/followUp")
	public ResponseEntity<FollowUpDataDto> getFollowUpData(@RequestHeader String spreadsheetId,
			@RequestParam int startingIndex, @RequestParam int maxRows, @RequestParam String status)
			throws IOException {
		return service.getFollowUpDetails(spreadsheetId, startingIndex, maxRows, status);
	}

	@ApiOperation(value = "To get status details by email ")
	@GetMapping("/followUpStatus")
	public List<StatusDto> getStatusByEmail(@RequestHeader String spreadsheetId, @RequestParam int startingIndex,
			@RequestParam int maxRows, @RequestParam String email, HttpServletRequest request) throws IOException {
		return service.getStatusDetails(spreadsheetId, startingIndex, maxRows, email, request);
	}

	@ApiOperation(value = "To get Registration details by email")
	@GetMapping("/getFollowUpEmail/{email}")
	public ResponseEntity<FollowUpDto> getFollowUpEmail(@RequestHeader String spreadsheetId, @PathVariable String email,
			HttpServletRequest request) throws IOException {
		return service.getFollowUpByEmail(spreadsheetId, email, request);
	}

	@ApiOperation(value = "To get Registration details by email")
	@GetMapping("/getFollowUpStatusByEmail/{email}")
	public ResponseEntity<List<StatusDto>> getFollowUpStatusByEmail(@RequestHeader String spreadsheetId,
			@PathVariable String email, HttpServletRequest request) throws IOException {
		List<StatusDto> list = service.getStatusDetailsByEmail(spreadsheetId, email, request);
		return ResponseEntity.ok(list);
	}

	@ApiOperation(value = "To update Birth day info while registering")
	@PostMapping("/birthDayInfo")
	public ResponseEntity<String> updateBirthDayInfo(@RequestHeader String spreadsheetId, @RequestBody TraineeDto dto,
			HttpServletRequest request) throws IllegalAccessException, IOException {
		return service.saveBirthDayInfo(spreadsheetId, dto, request);

	}

	@ApiOperation("to update the followup data")
	@PutMapping("/updateFollowUp")
	public ResponseEntity<String> updateFollowUp(@RequestHeader String spreadsheetId, @RequestParam String email,
			@RequestBody FollowUpDto dto, HttpServletRequest request) throws IOException, IllegalAccessException {

		return service.updateFollowUp(spreadsheetId, email, dto);
	}

	@ApiOperation(" Notification API for pending Follow Ups for the Day")
	@GetMapping("/notification")
	public ResponseEntity<SheetNotificationDto> getFollowupNotification(@RequestParam String email,
			HttpServletRequest request) throws IOException {

		ResponseEntity<SheetNotificationDto> entity = service.setNotification(email, request);
 
		return entity;

	}

	@ApiOperation(value = "To verifay the email")
	@GetMapping("/verify-email")
	public String verifydEmails(@RequestParam String email) throws IOException {
		String verifyEmails = service.verifyEmails(email);
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> jsonMap = objectMapper.readValue(verifyEmails, Map.class);
		String reasons = (String) jsonMap.get("reason");
		if (reasons.equals("accepted_email")) {
			return reasons;
		} else {
			return reasons;
		}

	}

	@ApiOperation(value = "To Add Enquiry Details")
	@PostMapping("/enquiry")

	public ResponseEntity<String> addEnquiry(@RequestBody EnquiryDto enquiryDto, @RequestHeader String spreadSheetId, HttpServletRequest request) {

		boolean saved = service.addEnquiry(enquiryDto, spreadSheetId, request);
		String uri = request.getRequestURI();
		System.out.println(uri.contains("enquiry"));
		System.out.println(enquiryDto);

		if (saved) {
			return ResponseEntity.ok().body("Enquiry Added Successfully");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save the enquiry");
		}
	}
}
