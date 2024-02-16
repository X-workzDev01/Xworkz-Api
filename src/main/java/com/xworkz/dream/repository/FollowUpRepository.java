package com.xworkz.dream.repository;

import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface FollowUpRepository {
	boolean saveToFollowUp(String spreadsheetId, List<Object> row);

	boolean updateFollowUpStatus(String spreadsheetId, List<Object> statusData);

	List<List<Object>> getFollowUpDetails(String spreadsheetId);

	boolean updateCurrentFollowUpStatus(String spreadsheetId, String currentFollowRange, List<Object> data);

	ValueRange getStatusId(String spreadsheetId);

	ValueRange getEmailList(String spreadsheetId);

	List<List<Object>> getEmailsAndNames(String spreadsheetId, String value);

	List<List<Object>> getFollowUpStatusDetails(String spreadsheetId);

	List<List<Object>> getFollowUpDetailsByid(String spreadsheetId);

	List<List<Object>> getFollowupStatusByDate(String spreadsheetId);

	UpdateValuesResponse updateFollow(String spreadsheetId, String range2, ValueRange valueRange);

}
