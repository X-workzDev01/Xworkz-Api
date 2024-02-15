package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.TraineeDto;

import freemarker.template.TemplateException;

 public interface RegistrationService {

	ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws MessagingException, TemplateException;

	ResponseEntity<String> emailCheck(String spreadsheetId, String email, HttpServletRequest request);

	ResponseEntity<String> contactNumberCheck(String spreadsheetId, Long contactNumber, HttpServletRequest request);

	ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows, String courseName,
			String collegeName);

	List<TraineeDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows);

	List<TraineeDto> filterData(String spreadsheetId, String searchValue, String courseName) throws IOException;

	ResponseEntity<String> update(String spreadsheetId, String email, TraineeDto dto);

	TraineeDto getDetailsByEmail(String spreadsheetId, String email) throws IOException;

	ResponseEntity<List<TraineeDto>> getSearchSuggestion(String spreadsheetId, String value, String courseName);


}
