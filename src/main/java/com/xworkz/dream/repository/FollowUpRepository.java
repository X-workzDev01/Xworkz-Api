package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface FollowUpRepository {
	boolean saveToFollowUp(String spreadsheetId, List<Object> row) throws IOException;

	boolean updateFollowUpStatus(String spreadsheetId, List<Object> statusData) throws IOException;

	List<List<Object>> getFollowUpDetails(String spreadsheetId) throws IOException;

	boolean updateCurrentFollowUpStatus(String spreadsheetId, String currentFollowRange, List<Object> data)
			throws IOException;

	ValueRange getStatusId(String spreadsheetId) throws IOException;

	ValueRange getEmailList(String spreadsheetId) throws IOException;

	List<List<Object>> getEmailsAndNames(String spreadsheetId, String value) throws IOException;

	List<List<Object>> getFollowUpStatusDetails(String spreadsheetId) throws IOException;

	List<List<Object>> getFollowUpDetailsByid(String spreadsheetId) throws IOException;

	List<List<Object>> getFollowupStatusByDate(String spreadsheetId) throws IOException;

	UpdateValuesResponse updateFollow(String spreadsheetId, String range2, ValueRange valueRange) throws IOException;

}
