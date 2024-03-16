package com.xworkz.dream.repository;

import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface BatchRepository {

	boolean saveBatchDetails(String spreadsheetId, List<Object> row);

	List<List<Object>> getCourseDetails(String spreadsheetId);

	UpdateValuesResponse updateBatchDetails(String spreadsheetId, String range2, ValueRange valueRange);

	ValueRange getCourseNameList(String spreadsheetId);

}
