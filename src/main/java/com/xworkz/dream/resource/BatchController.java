package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.BatchService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class BatchController {

	@Autowired
	private BatchService service;

	private Logger log = LoggerFactory.getLogger(BatchController.class);

	@GetMapping("/traineeDetails")
	@ApiOperation("To get the details of trainee based on the active course")
	public List<TraineeDto> traineeDetailsByCourse(@RequestHeader String spreadsheetId, @RequestParam String courseName)
			throws IOException {
		log.info("Request received for traineeDetailsByCourse. SpreadsheetId: {}, CourseName: {}", spreadsheetId,
				courseName);
		return service.getTraineeDetailsByCourse(spreadsheetId, courseName);
	}

	// suhas
	@GetMapping("/getCourseDetails")
	public ResponseEntity<BatchDetails> getBatchDetails(@RequestHeader String spreadsheetId,
			@RequestParam String courseName) throws IOException {
		log.info("Getting CourseDetails : {}", courseName);
		BatchDetails batchDetails = service.getBatchDetailsByCourseName(spreadsheetId, courseName);
		log.debug("Final batch detiles {}", batchDetails);
		return ResponseEntity.ok(batchDetails);

	}

	// suhas
	@GetMapping("/getCourseName")
	public ResponseEntity<List<Object>> getCourseName(@RequestHeader String spreadsheetId, @RequestParam String status)
			throws IOException {
		log.info("Getting CourseName : {}", status);
	
		return service.getCourseNameByStatus(spreadsheetId, status);
	}
}
