package com.xworkz.dream.resource;

import java.io.IOException;
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
	Logger logger = LoggerFactory.getLogger(DreamApiController.class);

	private FollowUpService service;


	@Autowired
	public FollowUpController(FollowUpService service) {
		this.service = service;
	}
	
	@ApiOperation(value = "To Update the follow Up status using ID")
	@PostMapping("/updateFollowStatus")
	public ResponseEntity<String> updateFollowUpStatus(@RequestHeader String spreadsheetId,
			@RequestBody StatusDto statusDto, HttpServletRequest request) throws IOException {
		logger.info("updating follow up status : {}", statusDto);
		return service.updateFollowUpStatus(spreadsheetId, statusDto);
	}
	
	@ApiOperation(value = "To get follow up details by pagination")
	@GetMapping("/followUp")
	public ResponseEntity<FollowUpDataDto> getFollowUpData(@RequestHeader String spreadsheetId,
			@RequestParam int startingIndex, @RequestParam int maxRows, @RequestParam String status,@RequestParam String courseName,@RequestParam String date)
			throws IOException {
		return service.getFollowUpDetails(spreadsheetId, startingIndex, maxRows, status,courseName,date);
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
	
	
	@ApiOperation("to update the followup data")
	@PutMapping("/updateFollowUp")
	public ResponseEntity<String> updateFollowUp(@RequestHeader String spreadsheetId, @RequestParam String email,
			@RequestBody FollowUpDto dto, HttpServletRequest request) throws IOException, IllegalAccessException {
		return service.updateFollowUp(spreadsheetId, email, dto);
	}
	
	
	
	@ApiOperation(value = "Get followup Status detiles By Date")
	@GetMapping("/getFollowupstatusByDate")
	public ResponseEntity<FollowUpDataDto> getFollowStatusByDate(@RequestParam String date,
			@RequestParam int startIndex, @RequestParam int endIndex, HttpServletRequest request) throws IOException {
		return service.getFollowStatusByDate(date, startIndex, endIndex, id, request);
	}
	
	
}