package com.xworkz.dream.service;

import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.dto.TraineeDto;

public interface EnquiryService {
	String writeDataEnquiry(String spreadsheetId, TraineeDto dto);

	String addEnquiry(EnquiryDto enquiryDto, String spreadsheetId);
}
