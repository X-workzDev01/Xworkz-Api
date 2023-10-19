package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;

public interface WhatsAppService {

	public BatchDetails getBatchDetailsListByCourseName(String spreadsheetId, String courseName) throws IOException;

	public boolean updateWhatsAppLinkByCourseName(String spreadsheetId, String cousreName, String whatsAppLink)
			throws IOException, IllegalAccessException;

	public List<String> getEmailByCourseName(String spreadsheetId, String cousreName)
			throws IOException;

	public boolean sendWhatsAppLink(String spreadsheetId, String courseName)throws IOException;
	
	public ResponseEntity<List<TraineeDto>> getTraineeDetailsByCourse(String spreadsheetId, String courseName)
			throws IOException;

	public ResponseEntity<List<FollowUpDto>> getTraineeDetailsByCourseInFollowUp(String spreadsheetId,
			String courseName) throws IOException;

	public ResponseEntity<List<FollowUpDto>> traineeDetailsByCourseAndStatusInFollowUp(String spreadsheetId,
			String courseName, String status) throws IOException;

	public ResponseEntity<List<FollowUpDto>> getGroupStatus(String spreadsheetId, String status) throws IOException;
}
