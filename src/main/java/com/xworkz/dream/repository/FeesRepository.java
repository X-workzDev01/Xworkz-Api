package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

public interface FeesRepository {
	boolean writeFeesDetiles(List<Object> list) throws IOException;

	List<List<Object>> getAllFeesDetiles(String getFeesDetiles) throws IOException;

	List<List<Object>> getFeesDetilesByemailInFollowup(String getFeesDetiles) throws IOException;

	String updateFeesDetiles(String getFeesDetilesfollowupRange, List<Object> values) throws IOException;

	ValueRange getEmailList(String spreadsheetId) throws IOException;

	boolean updateDetilesToFollowUp(String getFeesDetilesfollowupRange, List<Object> list) throws IOException;

}
