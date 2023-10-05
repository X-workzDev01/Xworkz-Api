package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.TraineeDto;

public interface WhatsAppService {
	
	public BatchDetails getBatchDetailsListByCourseName(String spreadsheetId, String courseName)throws IOException;

	public boolean updateWhatsAppLinkByCourseName(String spreadsheetId, String cousreName,String whatsAppLink) throws IOException, IllegalAccessException;

	public boolean getEmailByCourseName(String spreadsheetId, String cousreName)throws IOException;
}
