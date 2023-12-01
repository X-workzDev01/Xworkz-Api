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

	public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws MessagingException, TemplateException;

	public ResponseEntity<String> emailCheck(String spreadsheetId, String email, HttpServletRequest request);

	public ResponseEntity<String> contactNumberCheck(String spreadsheetId, Long contactNumber,
			HttpServletRequest request);

	public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows, String courseName);

	public List<TraineeDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows);

	public List<TraineeDto> filterData(String spreadsheetId, String searchValue) throws IOException;

	public ResponseEntity<String> update(String spreadsheetId, String email, TraineeDto dto);

	public TraineeDto getDetailsByEmail(String spreadsheetId, String email) throws IOException;

	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(String spreadsheetId, String value,
			HttpServletRequest request);

}
