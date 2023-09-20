package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
@Repository
public class AttendanceRepositoryImpl implements AttendanceRepository {
	private Sheets sheetsService;
	@Value("${sheets.attendanceInfoRange}")
	private String attendanceInfoRange;
	@Value("${sheets.attendanceInfoIDRange}")
	private String attendanceInfoIDRange;
	@Value("${sheets.attendanceInfoByName}")
	private String attendanceInfoByName;
	@Value("${sheets.attendanceList}")
	private String attendanceList;
	@Value("${sheets.attendanceListByEmail}")
	private String attendanceListByEmail;

	@Override
	public boolean writeAttendance(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, attendanceInfoRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	@Override
	public boolean everyDayAttendance(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();

		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, attendanceList, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	@Override
	public List<List<Object>> attendanceDetilesByEmail(String spreadsheetId, String email) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, attendanceListByEmail).execute();
		return response.getValues();
	}

}
