package com.xworkz.dream.resource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.service.FollowUpService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class FollowUpController {
	@Value("${login.sheetId}")
	private String id;
	private static final Logger log = LoggerFactory.getLogger(DreamApiController.class);

	private FollowUpService service;

	@Autowired
	public FollowUpController(FollowUpService service) {
		this.service = service;
	}

	@ApiOperation(value = "To Update the follow Up status using ID")
	@PostMapping("/updateFollowStatus")
	public ResponseEntity<String> updateFollowUpStatus(@RequestHeader String spreadsheetId,
			@RequestBody StatusDto statusDto){
		log.info("Updating follow-up status: {}", statusDto);
		return service.updateFollowUpStatus(spreadsheetId, statusDto);
	}

	@ApiOperation(value = "To get follow up details by pagination")
	@GetMapping("/followUp")
	public FollowUpDataDto getFollowUpData(@RequestHeader String spreadsheetId, @RequestParam int startingIndex,
			@RequestParam int maxRows, @RequestParam String status, @RequestParam String courseName,
			@RequestParam String date, @RequestParam String collegeName,@RequestParam String yearOfPass) {
		log.info(
				"Fetching follow-up details: spreadsheetId={}, startingIndex={}, maxRows={}, status={}, courseName={}, date={}, collegeName={},yearOfPass={}",
				spreadsheetId, startingIndex, maxRows, status, courseName, date,collegeName,yearOfPass);
		return service.getFollowUpDetails(spreadsheetId, startingIndex, maxRows, status, courseName, date, collegeName,yearOfPass);
	}

	@ApiOperation(value = "To get status details by email ")
	@GetMapping("/followUpStatus")
	public List<StatusDto> getStatusByEmail(@RequestHeader String spreadsheetId, @RequestParam int startingIndex,
			@RequestParam int maxRows, @RequestParam String email) {
		log.info("Fetching status details by email: spreadsheetId={}, startingIndex={}, maxRows={}, email={}",
				spreadsheetId, startingIndex, maxRows, email);
		return service.getStatusDetails(spreadsheetId, startingIndex, maxRows, email);
	}

	@ApiOperation(value = "To get Registration details by email")
	@GetMapping("/getFollowUpEmail/{email}")
	public ResponseEntity<FollowUpDto> getFollowUpEmail(@RequestHeader String spreadsheetId, @PathVariable String email)  {
		log.info("Fetching follow-up details by email: spreadsheetId={}, email={}", spreadsheetId, email);
		return service.getFollowUpByEmail(spreadsheetId, email);
	}

	@ApiOperation(value = "To get Registration details by email")
	@GetMapping("/getFollowUpStatusByEmail/{email}")
	public ResponseEntity<List<StatusDto>> getFollowUpStatusByEmail(@RequestHeader String spreadsheetId,
			@PathVariable String email) {
		log.info("Fetching follow-up status by email: spreadsheetId={}, email={}", spreadsheetId, email);
		List<StatusDto> list = service.getStatusDetailsByEmail(spreadsheetId, email);
		return ResponseEntity.ok(list);
	}

	@ApiOperation("to update the followup data")
	@PutMapping("/updateFollowUp")
	public ResponseEntity<String> updateFollowUp(@RequestHeader String spreadsheetId, @RequestParam String email,
			@RequestBody FollowUpDto dto)  {
		log.info("Updating follow-up data: spreadsheetId={}, email={}, dto={}", spreadsheetId, email, dto);
		return service.updateFollowUp(spreadsheetId, email, dto);
	}

	@GetMapping("/getTraineeDetails")
	@ApiOperation("To get the details of trainee based on the course in follow up")
	public FollowUpDataDto traineeDetailsByCourseInFollowUp(@RequestHeader String spreadsheetId,
			@RequestParam String courseName, @RequestParam int startingIndex, @RequestParam int maxRows) {
		log.info(
				"Fetching trainee details by course in follow-up: spreadsheetId={}, courseName={}, startingIndex={}, maxRows={}",
				spreadsheetId, courseName, startingIndex, maxRows);
		return service.getTraineeDetailsByCourseInFollowUp(spreadsheetId, courseName, startingIndex, maxRows);
	}

}
