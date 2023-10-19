package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;
import com.xworkz.dream.dto.BatchDetails;

public interface WhatsAppService {

	public BatchDetails getBatchDetailsListByCourseName(String spreadsheetId, String courseName) throws IOException;

	public boolean updateWhatsAppLinkByCourseName(String spreadsheetId, String cousreName, String whatsAppLink)
			throws IOException, IllegalAccessException;

	public List<String> getEmailByCourseName(String spreadsheetId, String cousreName)
			throws IOException;

	public boolean sendWhatsAppLink(String spreadsheetId, String courseName)throws IOException;
	
}
