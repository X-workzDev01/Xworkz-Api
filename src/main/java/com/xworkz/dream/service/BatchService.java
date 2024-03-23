package com.xworkz.dream.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.TraineeDto;

public interface BatchService {

	 ResponseEntity<String> saveDetails(String spreadsheetId, BatchDetailsDto dto);

	 List<TraineeDto> getTraineeDetailsByCourse(String spreadsheetId, String courseName);

	 BatchDetailsDto getBatchDetailsByCourseName(String spreadsheetId, String courseName);

	 BatchDetailsDto getBatchDetailsListByCourseName(String spreadsheetId, String courseName);

	 ResponseEntity<List<Object>> getCourseNameByStatus(String spreadsheetId, String status);
	
	 void updateBatchDetails(String courseName,BatchDetailsDto details);
	
	 Integer gettotalClassByCourseName(String courseName);
}

