package com.xworkz.dream.service;
import com.xworkz.dream.dto.BirthdayDataDto;

import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.TraineeDto;

public interface BirthadayService {

	void sendBirthdayEmails();

	String saveBirthDayInfo(TraineeDto dto);

	boolean updateDob(String email, TraineeDto dto);

	boolean updateBirthDayMailStatusYearly();

	boolean updateMailStatus(String email);
	BirthDayInfoDto getDetailsByEmail(String email) ;

	BirthdayDataDto getBirthdays(String spreadsheetId, int startingIndex, int maxRows, String date, String courseName,String month);

}
