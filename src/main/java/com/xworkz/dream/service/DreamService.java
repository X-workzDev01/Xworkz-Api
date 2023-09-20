package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.Team;

import freemarker.template.TemplateException;

public interface DreamService {


	public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws MessagingException, TemplateException;

	public boolean addToFollowUp(TraineeDto traineeDto, String spreadSheetId)
			throws IOException, IllegalAccessException;

	public ResponseEntity<String> emailCheck(String spreadsheetId, String email, HttpServletRequest request);

	public ResponseEntity<String> contactNumberCheck(String spreadsheetId, Long contactNumber,
			HttpServletRequest request);

	public void evictAllCaches();

	public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows);

	public List<TraineeDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows);

	public List<TraineeDto> filterData(String spreadsheetId, String searchValue) throws IOException;

	public ResponseEntity<String> update(String spreadsheetId, String email, TraineeDto dto);

	public ResponseEntity<String> updateFollowUp(String spreadsheetId, String email, FollowUpDto followDto)
			throws IOException, IllegalAccessException;

	public boolean updateCurrentFollowUp(String spreadsheetId, String email, String currentStatus,
			String currentlyFollowedBy) throws IOException, IllegalAccessException;

	public ResponseEntity<String> updateFollowUpStatus(String spreadsheetId, StatusDto statusDto,
			HttpServletRequest request);

	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(String spreadsheetId, String value,
			HttpServletRequest request);

	public ResponseEntity<?> getDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException;

	public ResponseEntity<FollowUpDto> getFollowUpByEmail(String spreadsheetId, String email,
			HttpServletRequest request) throws IOException;

	public ResponseEntity<FollowUpDataDto> getFollowUpDetails(String spreadsheetId, int startingIndex, int maxRows,
			String status) throws IOException;

	public List<FollowUpDto> getFollowUpRows(List<List<Object>> values, int startingIndex, int maxRows);

	public List<StatusDto> getStatusDetails(String spreadsheetId, int startingIndex, int maxRows, String email,
			HttpServletRequest request) throws IOException;

	public List<StatusDto> getStatusDetailsByEmail(String spreadsheetId, String email, HttpServletRequest request)
			throws IOException;

	public List<StatusDto> getFollowUpStatusData(List<List<Object>> values, int startingIndex, int maxRows);

	public ResponseEntity<String> saveDetails(String spreadsheetId, BatchDetailsDto dto, HttpServletRequest request)
			throws IOException, IllegalAccessException;

	public ResponseEntity<String> saveBirthDayInfo(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws IllegalAccessException, IOException;

	public ResponseEntity<List<Object>> getCourseNameByStatus(String spreadsheetId, String status);

	public ResponseEntity<BatchDetails> getBatchDetailsByCourseName(String spreadsheetId, String courseName);

	public FollowUpDto getFollowUpDetailsByEmail(String spreadsheetId, String email) throws IOException;


	public void notification();

	public void notification(String spreadsheetId, String email, List<Team> teamList, HttpServletRequest requests)
			throws IOException;

	public ResponseEntity<List<StatusDto>> setNotification(@Value("${myapp.scheduled.param}") String email,
			@Value("${myapp.scheduled.param}") HttpServletRequest requests) throws IOException;

	public String verifyEmails(String email);

}
