package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.DreamService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class DreamApiController {

	Logger logger = LoggerFactory.getLogger(DreamApiController.class);

	@Autowired
	private DreamService service;

	@ApiOperation(value = "To register the trainee details in the google sheets")
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestHeader String spreadsheetId, @RequestBody TraineeDto values,
			HttpServletRequest request) throws IOException {
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
	public ResponseEntity<List<Object>> getSearchSuggestion(@RequestHeader String spreadsheetId,
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

	@ApiOperation(value = "To get Registration details by email")
	@GetMapping("/readByEmail")
	public ResponseEntity<?> getDataByEmail(@RequestHeader String spreadsheetId, @RequestParam String email,
			HttpServletRequest request) throws IOException {
		return service.getDetailsByEmail(spreadsheetId, email, request);
	}

	@ApiOperation(value = "To get follow up details by pagination")
	@GetMapping("/followUp")
	public List<FollowUpDto> getFollowUpData(@RequestHeader String spreadsheetId, @RequestParam int startingIndex,
			@RequestParam int maxRows, @RequestParam String status) throws IOException {
		return service.getFollowUpDetails(spreadsheetId, startingIndex, maxRows, status);
	}

	@ApiOperation(value = "To get status details by email ")
	@GetMapping("/followStatus")
	public List<StatusDto> getStatusByEmail(@RequestHeader String spreadsheetId, @RequestParam int startingIndex,
			@RequestParam int maxRows, @RequestParam String email, HttpServletRequest request) throws IOException {
		System.out.println("this is getStatusdetails method");
		return service.getStatusDetails(spreadsheetId, startingIndex, maxRows, email, request);
	}
}
