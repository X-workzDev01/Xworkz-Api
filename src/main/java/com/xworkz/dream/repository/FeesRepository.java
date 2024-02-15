package com.xworkz.dream.repository;

import java.util.List;

public interface FeesRepository {
	boolean writeFeesDetiles(List<Object> list);

	List<List<Object>> getAllFeesDetiles(String getFeesDetiles);

	List<List<Object>> getFeesDetilesByemailInFollowup(String getFeesDetiles);

	String updateFeesDetiles(String getFeesDetilesfollowupRange, List<Object> values);

	List<List<Object>> getEmailList(String spreadsheetId);

	boolean updateDetilesToFollowUp(String getFeesDetilesfollowupRange, List<Object> list);

}
