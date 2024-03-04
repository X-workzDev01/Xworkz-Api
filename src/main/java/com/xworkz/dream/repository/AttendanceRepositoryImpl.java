package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.utils.SheetSaveOpration;

@Repository
public class AttendanceRepositoryImpl implements AttendanceRepository {
	private Sheets sheetsService;
	@Value("${login.sheetId}")
	private String sheetId;
	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private static final Logger log = LoggerFactory.getLogger(AttendanceRepositoryImpl.class);
	@Autowired
	private SheetSaveOpration saveOpration;

	@PostConstruct
	private void setSheetsService() throws Exception {
		sheetsService = saveOpration.ConnsetSheetService();
	}

	@Override
	public boolean writeAttendance(String spreadsheetId, List<Object> row, String range) {
		log.info("Writing attendance to sheet... :{} ", spreadsheetId);

		ValueRange value = null;
		try {
			value = sheetsService.spreadsheets().values().get(sheetId, range).execute();
		} catch (IOException e) {
			log.error("Cannot find Values {} ",e.getMessage());
		}
		if (value.getValues() != null && value.getValues().size() >= 1) {
			log.info("Attendance register sucessfully");
			System.err.println(range);
			return saveOpration.saveDetilesWithDataSize(row, range);

		} else {
			log.info("Attendance register sucessfully");
			return saveOpration.saveDetilesWithoutSize(row, range);
		}

	}

	@Override
	@Cacheable(value = "attendanceData", key = "'listOfAttendance'")
	public List<List<Object>> getAttendanceData(String spreadsheetId, String range) {
		log.info("Getting data from sheet : {} ", spreadsheetId);
		ValueRange response;
		try {
			response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
			return response.getValues();
		} catch (IOException e) {
			log.error("Error getting attendance data from sheet: {}", e.getMessage());
		}
		return null;

	}

	@Override
	public UpdateValuesResponse update(String spreadsheetId, String range, ValueRange valueRange) {
		log.info("Updating sheet data : {}", spreadsheetId);
		try {
			return sheetsService.spreadsheets().values().update(spreadsheetId, range, valueRange)
					.setValueInputOption("RAW").execute();
		} catch (IOException e) {
			log.error("Error updating sheet data: {}", e.getMessage());
		}
		return null;
	}

	@Override
	public boolean saveDetilesWithDataSize(List<Object> list, String attendanceRange) {
		List<List<Object>> values = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		rowData.add("");
		rowData.addAll(list.subList(1, list.size()));
		values.add(rowData);

		ValueRange body = new ValueRange().setValues(values);
		try {
			sheetsService.spreadsheets().values().append(sheetId, attendanceRange, body)
					.setValueInputOption("USER_ENTERED").execute();
			log.debug("registering fees repository data list is : {}", body);
			return true;
		} catch (IOException e) {
			log.error("Error saving attendance details with data size: {}", e.getMessage());
		}
		return false;

	}

	@Override
	public boolean saveDetilesWithoutSize(List<Object> list, String attendanceRange) {

		List<List<Object>> values = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		rowData.add("");
		rowData.addAll(list.subList(1, list.size()));
		values.add(rowData);

		ValueRange body = new ValueRange().setValues(values);
		try {
			sheetsService.spreadsheets().values().append(sheetId, attendanceRange, body)
					.setValueInputOption("USER_ENTERED").execute();
			log.debug("registering fees repository data list is : {}", body);
			return true;
		} catch (IOException e) {
			log.error("Error saving attendance details without data size: {}", e.getMessage());
		}
		return false;

	}

	@Override
	public List<List<Object>> getNamesAndCourseName(String spreadsheetId, String range, String value) {
		log.info("Reading names and courseName from sheet for spreadsheetId: {}", spreadsheetId);
		ValueRange response;
		try {
			response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
			return response.getValues();
		} catch (IOException e) {
			log.error("Error reading names and course names from sheet: {}", e.getMessage());
		}
		return null;

	}

}
