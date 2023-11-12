package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.BatchService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class BatchController {

	@Autowired
	private BatchService service;

	@GetMapping("/traineeDetails")
	@ApiOperation("To get the details of trainee based on the active course")
	public List<TraineeDto> traineeDetailsByCourse(@RequestHeader String spreadsheetId, @RequestParam String courseName)
			throws IOException {
		log.info("get details by course name {}", courseName);
		return service.getTraineeDetailsByCourse(spreadsheetId, courseName);
	}

	
	
	    // suhas
		@GetMapping("/getCourseDetails")
		public ResponseEntity<BatchDetails> getBatchDetails(@RequestHeader String spreadsheetId,
				@RequestParam String courseName) throws IOException {
			log.info("Getting CourseDetails : {}", courseName);
			return service.getBatchDetailsByCourseName(spreadsheetId, courseName);

		}

		// suhas
		@GetMapping("/getCourseName")
		public ResponseEntity<List<Object>> getCourseName(@RequestHeader String spreadsheetId, @RequestParam String status)
				throws IOException {
			log.info("Getting CourseName : {}", status);
			return service.getCourseNameByStatus(spreadsheetId, status);
		}
}
