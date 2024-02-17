package com.xworkz.dream.repository;

import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface FollowUpRepository {
	boolean saveToFollowUp(String spreadsheetId, List<Object> row);

	boolean updateFollowUpStatus(String spreadsheetId, List<Object> statusData);

	List<List<Object>> getFollowUpDetails(String spreadsheetId);

	boolean updateCurrentFollowUpStatus(String spreadsheetId, String currentFollowRange, List<Object> data);

	ValueRange getEmailList(String spreadsheetId);

	List<List<Object>> getFollowUpStatusDetails(String spreadsheetId);

	UpdateValuesResponse updateFollow(String spreadsheetId, String range2, ValueRange valueRange);

}
