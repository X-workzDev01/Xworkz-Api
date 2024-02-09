package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;

 public interface FollowUpService {

	 boolean addToFollowUp(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException;

	 ResponseEntity<String> updateFollowUp(String spreadsheetId, String email, FollowUpDto followDto)
			throws IOException, IllegalAccessException;

	 ResponseEntity<String> updateFollowUpStatus(String spreadsheetId, StatusDto statusDto);

	 ResponseEntity<FollowUpDto> getFollowUpByEmail(String spreadsheetId, String email,
			HttpServletRequest request) throws IOException;
	
	 FollowUpDataDto getTraineeDetailsByCourseInFollowUp(String spreadsheetId,
			String courseName,int startingIndex,int maxRows) throws IOException;
	
	 boolean addToFollowUpEnquiry(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException;

	 FollowUpDataDto getFollowUpDetails(String spreadsheetId, int startingIndex, int maxRows,
			String status, String courseName, String date, String collegeName) ;

	 List<FollowUpDto> getFollowUpRows(List<List<Object>> values, int startingIndex, int maxRows);

	 List<StatusDto> getFollowUpStatusData(List<List<Object>> values, int startingIndex, int maxRows);

	 FollowUpDto getFollowUpDetailsByEmail(String spreadsheetId, String email) throws IOException;

	 List<StatusDto> getStatusDetails(String spreadsheetId, int startingIndex, int maxRows, String email,
			HttpServletRequest request) throws IOException;

	 List<StatusDto> getStatusDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException;

	boolean updateCurrentFollowUp(String calback, String spreadsheetId, String email, String currentStatus,
			String currentlyFollowedBy, String joiningDate) throws IOException, IllegalAccessException;

	boolean updateFollowUp(String spreadsheetId, String email, TraineeDto dto)
			throws IOException, IllegalAccessException;

	boolean addCsrToFollowUp(TraineeDto traineeDto, String spreadSheetId) throws IOException, IllegalAccessException;

}
