package com.xworkz.dream.repository;

import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface AttendanceRepository {
	boolean writeAttendance(String spreadsheetId, List<Object> row, String range);

	List<List<Object>> getAttendanceData(String spreadsheetId, String attendanceInfoRange);

	UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange);

	boolean saveDetilesWithDataSize(List<Object> list, String attendanceRange);

	boolean saveDetilesWithoutSize(List<Object> list, String attendanceRange);

	List<List<Object>> getNamesAndCourseName(String spreadsheetId, String range, String value);

	

}
