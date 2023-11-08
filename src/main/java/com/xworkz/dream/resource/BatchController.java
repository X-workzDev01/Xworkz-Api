package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.BatchService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class BatchController {
	
	@Autowired
	private BatchService service;
	
	
	Logger logger = LoggerFactory.getLogger(CreateBatchInfoController.class);
	@GetMapping("/traineeDetails")
	@ApiOperation("To get the details of trainee based on the active course")
	public List<TraineeDto> traineeDetailsByCourse(@RequestHeader String spreadsheetId,
			@RequestParam String courseName) throws IOException {
		return service.getTraineeDetailsByCourse(spreadsheetId, courseName);
	}	
	
	@GetMapping("/getTraineeDetails")
	@ApiOperation("To get the details of trainee based on the course in follow up")
	public FollowUpDataDto traineeDetailsByCourseInFollowUp(@RequestHeader String spreadsheetId,
			@RequestParam String courseName) throws IOException {
		return service.getTraineeDetailsByCourseInFollowUp(spreadsheetId, courseName);
	}
	
	@GetMapping("/getByCourseAndStatus")
	@ApiOperation("To get the details of trainee based on the course and status in follow up")
	public FollowUpDataDto traineeDetailsByCourseAndStatusInFollowUp(@RequestHeader String spreadsheetId,
			@RequestParam String courseName,String status,@RequestParam String date,   int startingIndex,int maxRows) throws IOException {
		System.out.println("this is getByCourseAndStatus:"+courseName+" "+status);
		return service.traineeDetailsByCourseAndStatusInFollowUp(spreadsheetId, courseName,status,date,startingIndex,maxRows);
	}

}
