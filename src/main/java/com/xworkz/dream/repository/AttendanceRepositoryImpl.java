package com.xworkz.dream.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchGetValuesByDataFilterRequest;
import com.google.api.services.sheets.v4.model.BatchGetValuesByDataFilterResponse;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.DataFilter;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Repository
public class AttendanceRepositoryImpl implements AttendanceRepository {
	private Sheets sheetsService;
	@Value("${login.sheetId}")
	private String sheetId;
	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	@Autowired
	private ResourceLoader resourceLoader;

	@PostConstruct
	private void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException {

		Resource resource = resourceLoader.getResource(credentialsPath);
		File file = resource.getFile();

		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(file)).createScoped(SCOPES);

		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
		sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
				requestInitializer).setApplicationName(applicationName).build();
	}

	@Override
//	@CachePut(value = "writeAttendance", key = "#spreadsheetId", unless = "#result == null")
	public boolean writeAttendance(String spreadsheetId, List<Object> row, String range) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		System.err.println("row                  " + row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, range, body).setValueInputOption("USER_ENTERED")
				.execute();
		return true;
	}

	@Override
//	@Cacheable(value = "byEmail", key = "#sheetId", unless = "#result == null")

	public List<List<Object>> attendanceDetilesByEmail(String sheetId, String email, String range) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(sheetId, range).execute();
		return response.getValues();
	}

	@Override
//	@CachePut(value = "everyDay", key = "#spreadsheetId", unless = "#result == null")
	public boolean everyDayAttendance(String spreadsheetId, List<Object> row, String range) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, range, body).setValueInputOption("USER_ENTERED")
				.execute();
		return true;
	}

	@Override
//	@Cacheable(value = "getEmail", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getEmail(String spreadsheetId, String range) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
		return response.getValues();
	}

	@Override
//	@CachePut(value = "update", key = "#spreadsheetId", unless = "#result == null")
	public UpdateValuesResponse update(String spreadsheetId, String range, ValueRange valueRange) throws IOException {
		return sheetsService.spreadsheets().values().update(spreadsheetId, range, valueRange).setValueInputOption("RAW")
				.execute();
	}

	@Override
	public void clearColumnData(String spreadsheetId, String range) throws IOException {
		Sheets.Spreadsheets.Values.Clear request = sheetsService.spreadsheets().values().clear(spreadsheetId, range,
				new ClearValuesRequest());
		request.execute();
	}
}
