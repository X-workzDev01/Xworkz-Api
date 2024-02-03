package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

public interface WhatsAppService {


	public boolean updateWhatsAppLinkByCourseName(String spreadsheetId, String cousreName, String whatsAppLink)
			throws IOException, IllegalAccessException;

	public List<String> getEmailByCourseName(String spreadsheetId, String cousreName)
			throws IOException;

	public Boolean sendWhatsAppLink(String spreadsheetId, String courseName)throws IOException;
	
	public Boolean updateWhatsAppLinkByBatchName(String courseName,String whatsAppLink) throws IllegalAccessException, IOException ;
	
}
