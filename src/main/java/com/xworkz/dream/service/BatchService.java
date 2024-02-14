package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.TraineeDto;

public interface BatchService {

	 ResponseEntity<String> saveDetails(String spreadsheetId, BatchDetailsDto dto, HttpServletRequest request)
			throws IOException, IllegalAccessException;

	 List<TraineeDto> getTraineeDetailsByCourse(String spreadsheetId, String courseName) throws IOException;

	 BatchDetailsDto getBatchDetailsByCourseName(String spreadsheetId, String courseName) throws IOException;

	 BatchDetailsDto getBatchDetailsListByCourseName(String spreadsheetId, String courseName) throws IOException;

	 ResponseEntity<List<Object>> getCourseNameByStatus(String spreadsheetId, String status);
	
	 void updateBatchDetails(String courseName,BatchDetailsDto details) throws IOException,IllegalAccessException;
	
	 Integer gettotalClassByCourseName(String courseName) throws IOException;
}

