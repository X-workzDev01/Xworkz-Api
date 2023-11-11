package com.xworkz.dream.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
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
public class DreamRepositoryImpl implements DreamRepository {

	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private Sheets sheetsService;
	@Value("${sheets.range}")
	private String range;
	@Value("${sheets.emailRange}")
	private String emailRange;
	@Value("${sheets.contactNumberRange}")
	private String contactNumberRange;
	@Value("${sheets.dropdownRange}")
	private String dropdownRange;
	@Value("${sheets.idRange}")
	private String idRange;
	@Value("${sheets.loginInfoRange}")
	private String loginInfoRange;
	@Value("${sheets.followUpRange}")
	private String followUpRange;

	
	@Value("${sheets.batchDetails}")
	private String batchDetails;
	@Value("${sheets.batchDetailsRange}")
	private String batchDetailsRange;
	@Value("${sheets.batchIdRange}")
	private String batchIdRange;
	@Value("${sheets.dateOfBirthDetailsRange}")
	private String dateOfBirthDetailsRange;
	@Value("${sheets.birthdayRange}")
	private String birthdayRange;
	@Value("${sheets.followUpEmailRange}")
	private String followUpEmailRange;
	@Value("${sheets.followUpStatusIdRange}")
	private String followUpStatusIdRange;

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	@PostConstruct
	public void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException {

		Resource resource = resourceLoader.getResource(credentialsPath);
		File file = resource.getFile();

		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(file)).createScoped(SCOPES);

		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
		sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
				requestInitializer).setApplicationName(applicationName).build();
	}

	@Override
	public boolean writeData(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		// Add an empty string as a placeholder for the A column
		List<Object> rowData = new ArrayList<>();
		rowData.add(""); // Placeholder for A column
		rowData.addAll(row.subList(1, row.size())); // Start from the second element (B column)
		values.add(rowData);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, range, body).setValueInputOption("USER_ENTERED")
				.execute();
		return true;
	}

	@Override
//	@Cacheable(value = "emailData", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getEmails(String spreadsheetId, String email) throws IOException {
		ValueRange emailValue = sheetsService.spreadsheets().values().get(spreadsheetId, emailRange).execute();
		return emailValue.getValues();
	}

	@Override
//	@Cacheable(value = "contactData", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getContactNumbers(String spreadsheetId) throws IOException {
		 ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, contactNumberRange).execute();
		return response.getValues();
	}

	@Override
	public ValueRange getIds(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, idRange).execute();

		return response;
	}

	@Override
	@Cacheable(value = "getDropdowns", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getDropdown(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, dropdownRange).execute();
		return response.getValues();
	}

	@Override
	public boolean updateLoginInfo(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);

		sheetsService.spreadsheets().values().append(spreadsheetId, loginInfoRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;

	}
 
	@Override
//	@Cacheable(value = "sheetsData", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> readData(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> data = response.getValues();
		return data;
	}

	@Override
	public UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange) throws IOException {
		UpdateValuesResponse response = sheetsService.spreadsheets().values().update(spreadsheetId, range2, valueRange)
				.setValueInputOption("RAW").execute();
		return response;
	}

	
	
	@Override
	public ValueRange getBatchId(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, batchIdRange).execute();
		return response;
	}

	@Override
	public boolean saveBatchDetails(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, batchDetailsRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	} 

	@Override
	public ValueRange getBirthDayId(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, birthdayRange).execute();
		return response;
	}

	@Override
	public boolean saveBirthDayDetails(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> values = new ArrayList<>();
		// Add an empty string as a placeholder for the A column
		List<Object> rowData = new ArrayList<>();
		rowData.add(""); // Placeholder for A column
		rowData.addAll(row.subList(1, row.size())); // Start from the second element (B column)
		values.add(rowData);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, dateOfBirthDetailsRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	

	@Override
	public ValueRange getEmailList(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpEmailRange).execute();
		return response;
	}

	@Override
	@Cacheable(value = "batchDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getCourseDetails(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, batchDetailsRange).execute();
		return response.getValues();
	}

	@Override
	public List<List<Object>> notification(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpStatus).execute();
		return response.getValues();
	}

	

	@Override
	public List<List<Object>> getBirthadayDetails(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, dateOfBirthDetailsRange)
				.execute();
		return response.getValues();
	}



}
