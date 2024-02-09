package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

public interface WhatsAppService {

	boolean updateWhatsAppLinkByCourseName(String spreadsheetId, String cousreName, String whatsAppLink)
			throws IOException, IllegalAccessException;

	List<String> getEmailByCourseName(String spreadsheetId, String cousreName) throws IOException;

	Boolean sendWhatsAppLink(String spreadsheetId, String courseName) throws IOException;

	Boolean updateWhatsAppLinkByBatchName(String courseName, String whatsAppLink)
			throws IllegalAccessException, IOException;

}
