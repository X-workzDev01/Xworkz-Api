package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface BatchRepository {


	public boolean saveBatchDetails(String spreadsheetId, List<Object> row) throws IOException;

	public List<List<Object>> getCourseDetails(String spreadsheetId) throws IOException;

	public UpdateValuesResponse updateBatchDetails(String spreadsheetId, String range2, ValueRange valueRange)
			throws IOException;

	public ValueRange getCourseNameList(String spreadsheetId) throws IOException;

}
