package com.xworkz.dream.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface RegisterRepository {
	

	public void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException;
	
	public boolean writeData(String spreadsheetId, List<Object> row) throws IOException;

	public List<List<Object>>  getEmails(String spreadsheetId ,String email) throws IOException;

	public List<List<Object>>  getContactNumbers(String spreadsheetId) throws IOException;
	
	public List<List<Object>> readData(String spreadsheetId) throws IOException;

	public UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange) throws IOException;
	
	public List<List<Object>> getEmailsAndNames(String spreadsheetId, String value) throws IOException;

	List<List<Object>> getAlternativeNumber(String spreadsheetId) throws IOException;


}
