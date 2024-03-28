package com.xworkz.dream.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;

 public interface FollowUpService {

	 boolean addToFollowUp(TraineeDto traineeDto, String spreadSheetId);
	 ResponseEntity<String> updateFollowUp(String spreadsheetId, String email, FollowUpDto followDto);

	 ResponseEntity<String> updateFollowUpStatus(String spreadsheetId, StatusDto statusDto);

	 ResponseEntity<FollowUpDto> getFollowUpByEmail(String spreadsheetId, String email) ;
	
	 FollowUpDataDto getTraineeDetailsByCourseInFollowUp(String spreadsheetId,
			String courseName,int startingIndex,int maxRows) ;
	
	 boolean addToFollowUpEnquiry(TraineeDto traineeDto, String spreadSheetId);

	 FollowUpDataDto getFollowUpDetails(String spreadsheetId, int startingIndex, int maxRows,
			String status, String courseName, String date, String collegeName,String yearOfPass) ;

	 List<FollowUpDto> getFollowUpRows(List<List<Object>> values, int startingIndex, int maxRows);

	 List<StatusDto> getFollowUpStatusData(List<List<Object>> values, int startingIndex, int maxRows);

	 FollowUpDto getFollowUpDetailsByEmail(String spreadsheetId, String email);

	 List<StatusDto> getStatusDetails(String spreadsheetId, int startingIndex, int maxRows, String email);

	 List<StatusDto> getStatusDetailsByEmail(String spreadsheetId, String email);

	boolean updateCurrentFollowUp(String calback, String spreadsheetId, String email, String currentStatus,
			String currentlyFollowedBy, String joiningDate);

	boolean updateFollowUp(String spreadsheetId, String email, TraineeDto dto);

	boolean addCsrToFollowUp(TraineeDto traineeDto, String spreadSheetId);

}
