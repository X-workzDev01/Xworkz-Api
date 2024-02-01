package com.xworkz.dream.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
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
	private static final Logger log = LoggerFactory.getLogger(AttendanceRepositoryImpl.class);
	@PostConstruct
	private void setSheetsService() throws Exception  {

		Resource resource = resourceLoader.getResource(credentialsPath);
		File file = resource.getFile();

		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(file)).createScoped(SCOPES);

		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
		sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
				requestInitializer).setApplicationName(applicationName).build();
		 log.info("Sheets service setup complete.");
	}

	@Override
	public boolean writeAttendance(String spreadsheetId, List<Object> row, String range) throws IOException {
		  log.info("Writing attendance to sheet...");

			ValueRange value = sheetsService.spreadsheets().values().get(sheetId, range).execute();
			if (value.getValues() != null && value.getValues().size() >= 1) {
				log.info("Attendance register sucessfully");
				return this.saveDetilesWithDataSize(row, range);

			} else {
				log.info("Attendance register sucessfully");
				return this.saveDetilesWithoutSize(row, range);
			}
	}
	

	@Override
	@Cacheable(value = "attendanceData", key = "'listOfAttendance'")
	public List<List<Object>> getAttendanceData(String spreadsheetId, String range) throws IOException {
		 log.info("Getting data from sheet...");
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
		log.info("Data retrieved successfully.");
		return response.getValues();
	}

	@Override
	public UpdateValuesResponse update(String spreadsheetId, String range, ValueRange valueRange) throws IOException {
		 log.info("Updating sheet data...");
		 log.error("valueRange : "+valueRange);
		return sheetsService.spreadsheets().values().update(spreadsheetId, range, valueRange).setValueInputOption("RAW")
				.execute();
	}
	
	
	@Override
	public boolean saveDetilesWithDataSize(List<Object> list, String attendanceRange) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		rowData.add("");
		rowData.addAll(list.subList(1, list.size()));
		values.add(rowData);

		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append( sheetId , attendanceRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		log.debug("registering fees repository data list is : {}", body);

		return true;

	}
	
	@Override
	public boolean saveDetilesWithoutSize(List<Object> list, String attendanceRange) throws IOException {

		List<List<Object>> values = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		rowData.add("");
		rowData.addAll(list.subList(1, list.size()));
		values.add(rowData);

		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append( sheetId , attendanceRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		log.debug("registering fees repository data list is : {}", body);
		return true;

	}


}
