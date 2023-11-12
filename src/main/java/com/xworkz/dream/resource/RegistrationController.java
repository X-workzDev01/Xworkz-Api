package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class RegistrationController {
	
	@Value("${login.sheetId}")
	private String id;
	Logger logger = LoggerFactory.getLogger(DreamApiController.class);

	private RegistrationService service;

	@Autowired
	private CacheManager manager;

	@Autowired
	public RegistrationController(RegistrationService service) {
		this.service = service;
	}
	                                                                      
	@ApiOperation(value = "To register the trainee details in the google sheets")
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestHeader String spreadsheetId, @RequestBody TraineeDto values,
			HttpServletRequest request) throws IOException, MessagingException, TemplateException {
		logger.debug("Registering trainee details: {}", values);

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

	

	@ApiOperation(value = "To get Registration details by email")
	@GetMapping("/readByEmail")
	public ResponseEntity<?> getDataByEmail(@RequestHeader String spreadsheetId, @RequestParam String email,
			HttpServletRequest request) throws IOException {
		return service.getDetailsByEmail(spreadsheetId, email, request);
	}
	

}
