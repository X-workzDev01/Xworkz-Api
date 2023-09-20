package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

public interface AttendanceRepository {
	public boolean writeAttendance(String spreadsheetId, List<Object> row) throws IOException;

	public boolean everyDayAttendance(String spreadsheetId, List<Object> row) throws IOException;

	public List<List<Object>> attendanceDetilesByEmail(String spreadsheetId, String email) throws IOException;

}
