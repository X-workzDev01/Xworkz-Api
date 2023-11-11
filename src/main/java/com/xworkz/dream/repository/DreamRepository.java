package com.xworkz.dream.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface DreamRepository {

	public void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException;

	public boolean writeData(String spreadsheetId, List<Object> row) throws IOException;

	public ValueRange getEmails(String spreadsheetId) throws IOException;

	public ValueRange getContactNumbers(String spreadsheetId) throws IOException;

	public ValueRange getIds(String spreadsheetId) throws IOException;

	public List<List<Object>> getDropdown(String spreadsheetId) throws IOException;

	public boolean updateLoginInfo(String spreadsheetId, List<Object> row) throws IOException;

	public List<List<Object>> readData(String spreadsheetId) throws IOException;

	public UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange) throws IOException;

    public ValueRange getBatchId(String spreadsheetId) throws IOException;

	public boolean saveBatchDetails(String spreadsheetId, List<Object> row) throws IOException;

	public ValueRange getBirthDayId(String spreadsheetId) throws IOException;

	public boolean saveBirthDayDetails(String spreadsheetId, List<Object> row) throws IOException;

	public ValueRange getEmailList(String spreadsheetId) throws IOException;

	public List<List<Object>> getCourseDetails(String spreadsheetId) throws IOException;

	public List<List<Object>> notification(String spreadsheetId) throws IOException;


}
