package com.xworkz.dream.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.TraineeDto;

public interface BirthadayService {

	public void sendBirthdayEmails() throws IOException;

	public ResponseEntity<String> saveBirthDayInfo(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws IllegalAccessException, IOException;

}
