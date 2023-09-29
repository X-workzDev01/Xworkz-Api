package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface AttendanceRepository {
	public boolean writeAttendance(String spreadsheetId, List<Object> row, String range) throws IOException;

	List<List<Object>> getEmail(String spreadsheetId, String attendanceInfoRange) throws IOException;

	public boolean everyDayAttendance(String spreadsheetId, List<Object> row, String range) throws IOException;

	public List<List<Object>> attendanceDetilesByEmail(String spreadsheetId, String email, String range)
			throws IOException;

	public UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange) throws IOException;

	public void clearColumnData(String spreadsheetId, String range) throws IOException;

	public void evictCacheByEmail() throws IOException;
}
