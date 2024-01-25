package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.TraineeDto;

public interface BatchService {

	public ResponseEntity<String> saveDetails(String spreadsheetId, BatchDetailsDto dto, HttpServletRequest request)
			throws IOException, IllegalAccessException;

	public List<TraineeDto> getTraineeDetailsByCourse(String spreadsheetId, String courseName) throws IOException;

	public BatchDetailsDto getBatchDetailsByCourseName(String spreadsheetId, String courseName) throws IOException;

	public BatchDetailsDto getBatchDetailsListByCourseName(String spreadsheetId, String courseName) throws IOException;

	public ResponseEntity<List<Object>> getCourseNameByStatus(String spreadsheetId, String status);
	
	public void updateBatchDetails(String courseName,BatchDetailsDto details) throws IOException,IllegalAccessException;
	
	public Integer gettotalClassByCourseName(String courseName) throws IOException;
}

