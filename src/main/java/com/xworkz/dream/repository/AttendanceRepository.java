package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface AttendanceRepository {
	public boolean writeAttendance(String spreadsheetId, List<Object> row, String range) throws IOException;

	List<List<Object>> getId(String spreadsheetId, String attendanceInfoRange) throws IOException;


	public UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange) throws IOException;



}
