package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface FollowUpRepository {
	public boolean saveToFollowUp(String spreadsheetId, List<Object> row) throws IOException;
	
	public boolean updateFollowUpStatus(String spreadsheetId, List<Object> statusData) throws IOException;
	
	public List<List<Object>> getFollowUpDetails(String spreadsheetId) throws IOException;
	
	public boolean updateCurrentFollowUpStatus(String spreadsheetId, String currentFollowRange, List<Object> data)
			throws IOException;
	
	public ValueRange getStatusId(String spreadsheetId) throws IOException;
	
	public ValueRange getEmailList(String spreadsheetId) throws IOException;
	
	public List<List<Object>> getEmailsAndNames(String spreadsheetId, String value) throws IOException;
	
	public List<List<Object>> getFollowUpStatusDetails(String spreadsheetId) throws IOException;

	public List<List<Object>> getFollowUpDetailsByid(String spreadsheetId) throws IOException;

	public List<List<Object>> getFollowupStatusByDate(String spreadsheetId) throws IOException;

	public UpdateValuesResponse updateFollow(String spreadsheetId, String range2, ValueRange valueRange)
			throws IOException;

	void evictFollowUpStatusDetails();
}
