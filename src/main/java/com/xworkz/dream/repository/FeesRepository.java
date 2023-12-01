package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

public interface FeesRepository {
	public boolean writeFeesDetiles(List<Object> list) throws IOException;

	public List<List<Object>> getAllFeesDetiles(String getFeesDetiles) throws IOException;

	public List<List<Object>> getFeesDetilesByemailInFollowup(String getFeesDetiles) throws IOException;

	public String updateFeesDetiles(String getFeesDetilesfollowupRange, List<Object> values) throws IOException;

	public ValueRange getEmailList(String spreadsheetId) throws IOException;

	public boolean updateDetilesToFollowUp(String getFeesDetilesfollowupRange, List<Object> list) throws IOException;

}
