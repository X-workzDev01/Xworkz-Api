package com.xworkz.dream.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.TraineeDto;

public interface BirthadayService {

	void sendBirthdayEmails() throws IOException;

	ResponseEntity<String> saveBirthDayInfo(String spreadsheetId, TraineeDto dto)
			throws IllegalAccessException, IOException;

	boolean updateDob(TraineeDto dto);

}
