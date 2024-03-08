package com.xworkz.dream.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.RegistrationService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class RegistrationController {

	@Value("${login.sheetId}")
	private String id;
	private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

	private RegistrationService service;

	@Autowired
	public RegistrationController(RegistrationService service) {
		this.service = service;
	}

	@ApiOperation(value = "To register the trainee details in the google sheets")
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestHeader String spreadsheetId, @RequestBody TraineeDto values,
			HttpServletRequest request) {
		log.info("Received request to register trainee details: {}", values);

		return service.writeData(spreadsheetId, values);
	}

	@ApiOperation(value = "To register Check whether email already exist while registering")
	@GetMapping("/emailCheck")
	public ResponseEntity<String> emailCheck(@RequestHeader String spreadsheetId, @RequestParam String email) {
		log.info("Checking email existence: {}", email);
		return service.emailCheck(spreadsheetId, email);
	}

	@ApiOperation(value = "To get Suggestions while search")
	@GetMapping("register/suggestion/{courseName}")
	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(@RequestHeader String spreadsheetId,
			@RequestParam String value, @PathVariable String courseName, @RequestParam String collegeName,
			@RequestParam String followupStatus) {
		log.info("Getting suggestions for search: {},courseName:{},collegeName:{}", value, courseName, collegeName);
		return service.getSearchSuggestion(spreadsheetId, value, courseName, collegeName, followupStatus);
	}

	@ApiOperation(value = "To register Check whether contact number already exist while registering")
	@GetMapping("/contactNumberCheck")
	public ResponseEntity<String> contactNumberCheck(@RequestHeader String spreadsheetId,
			@RequestParam Long contactNumber) {
		log.info("Checking contact number existence: {}", contactNumber);
		return service.contactNumberCheck(spreadsheetId, contactNumber);
	}

	@GetMapping("/readData")
	public ResponseEntity<SheetsDto> readData(@RequestHeader String spreadsheetId, @RequestParam int startingIndex,
			@RequestParam int maxRows, @RequestParam String courseName, @RequestParam String collegeName,
			@RequestParam String followupStatus) {
		log.info("Reading data with parameters - SpreadsheetId: {}, Starting Index: {}, Max Rows: {}, Course Name: {}",
				spreadsheetId, startingIndex, maxRows, courseName);
		return service.readData(spreadsheetId, startingIndex, maxRows, courseName, collegeName, followupStatus);
	}

	@GetMapping("/filterData/{courseName}")
	public List<TraineeDto> filterData(@RequestHeader String spreadsheetId, @PathVariable String courseName,
			@RequestParam String searchValue, @RequestParam String collegeName, @RequestParam String followupStatus) {
		log.info("Filtering data with parameters-> Search Value: {},collegeName:{},courseName:{}", searchValue,
				collegeName, courseName);
		return service.filterData(spreadsheetId, searchValue, courseName, collegeName, followupStatus);
	}

	@ApiOperation(value = "To Update Registrated Details of Trainee")
	@PutMapping("/update")
	public ResponseEntity<String> update(@RequestHeader String spreadsheetId, @RequestParam String email,
			@RequestBody TraineeDto dto) {
		log.info("Updating trainee details with parameters - SpreadsheetId: {}, Email: {}, TraineeDto: {}",
				spreadsheetId, email, dto);
		return service.update(spreadsheetId, email, dto);
	}

	@ApiOperation(value = "To get Registration details by email")
	@GetMapping("/readByEmail")
	public ResponseEntity<?> getDataByEmail(@RequestHeader String spreadsheetId, @RequestParam String email) {
		log.info("Getting details by email - SpreadsheetId: {}, Email: {}", spreadsheetId, email);
		return ResponseEntity.ok(service.getDetailsByEmail(spreadsheetId, email));

	}

	@ApiOperation(value="To Check X-workz email Id")
	@GetMapping("/checkxworkzemail")
	public ResponseEntity<String> checkworkzEmail(@RequestParam String email) {
	    log.info("Checking x-workz email existence: {}", email);
	    String response = service.checkworkzEmail(email);
	    return ResponseEntity.ok(response);
	}

}
