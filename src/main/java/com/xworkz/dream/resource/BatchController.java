package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.FollowUpDto;
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
	public List<FollowUpDto> traineeDetailsByCourseInFollowUp(@RequestHeader String spreadsheetId,
			@RequestParam String courseName) throws IOException {
		return service.getTraineeDetailsByCourseInFollowUp(spreadsheetId, courseName);
	}
	
	@GetMapping("/getByCourseAndStatus")
	@ApiOperation("To get the details of trainee based on the course and status in follow up")
	public List<FollowUpDto> traineeDetailsByCourseAndStatusInFollowUp(@RequestHeader String spreadsheetId,
			@RequestParam String courseName,String status) throws IOException {
		System.out.println("this is getByCourseAndStatus:"+courseName+" "+status);
		return service.traineeDetailsByCourseAndStatusInFollowUp(spreadsheetId, courseName,status);
	}
	
	@GetMapping("/getGroupStatus")
	@ApiOperation("To get the details by groupped status")
	public List<FollowUpDto> getGroupStatus(@RequestHeader String spreadsheetId,
			String status) throws IOException {
		System.out.println("this is getByCourseAndStatus:"+status);
		return service.getGroupStatus(spreadsheetId,status);
	}
	
	@GetMapping("/cache")
	@ApiOperation("To get data from cache")
	public List<List<Object>> getCache(@RequestHeader String spreadsheetId) throws IOException{
		return service.getList(spreadsheetId);
	} 

}
