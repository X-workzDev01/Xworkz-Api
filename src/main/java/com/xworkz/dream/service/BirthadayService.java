package com.xworkz.dream.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.xworkz.dream.dto.BirthdayDataDto;
import com.xworkz.dream.dto.TraineeDto;

public interface BirthadayService {

	void sendBirthdayEmails() throws IOException;

	ResponseEntity<String> saveBirthDayInfo(String spreadsheetId, TraineeDto dto)
			throws IllegalAccessException, IOException;

	boolean updateDob(TraineeDto dto);

	BirthdayDataDto getBirthdays(String spreadsheetId, int startingIndex, int maxRows, String date, String courseName);

}
