package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.Team;

import freemarker.template.TemplateException;

public interface DreamService {

	public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws MessagingException, TemplateException;

	

	public ResponseEntity<String> emailCheck(String spreadsheetId, String email, HttpServletRequest request);

	public ResponseEntity<String> contactNumberCheck(String spreadsheetId, Long contactNumber,
			HttpServletRequest request);

	public void evictAllCaches();

	public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows);

	public List<TraineeDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows);

	public List<TraineeDto> filterData(String spreadsheetId, String searchValue) throws IOException;

	public ResponseEntity<String> update(String spreadsheetId, String email, TraineeDto dto);


	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(String spreadsheetId, String value,
			HttpServletRequest request);

	public ResponseEntity<?> getDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException;

	
	public List<StatusDto> getStatusDetails(String spreadsheetId, int startingIndex, int maxRows, String email,
			HttpServletRequest request) throws IOException;

	public List<StatusDto> getStatusDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException;

	
	public ResponseEntity<String> saveDetails(String spreadsheetId, BatchDetailsDto dto, HttpServletRequest request)
			throws IOException, IllegalAccessException;

	public ResponseEntity<String> saveBirthDayInfo(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws IllegalAccessException, IOException;

	public ResponseEntity<List<Object>> getCourseNameByStatus(String spreadsheetId, String status);

	public ResponseEntity<BatchDetails> getBatchDetailsByCourseName(String spreadsheetId, String courseName)
			throws IOException;

	
	public void notification();

	public ResponseEntity<SheetNotificationDto> notification(String spreadsheetId, String email, List<Team> teamList,
			HttpServletRequest requests) throws IOException;

	public ResponseEntity<SheetNotificationDto> setNotification(@Value("${myapp.scheduled.param}") String email,
			@Value("${myapp.scheduled.param}") HttpServletRequest requests) throws IOException;

	public String verifyEmails(String email);

	public boolean addEnquiry(EnquiryDto enquiryDto, String spreadsheetId, HttpServletRequest request);

	
}
