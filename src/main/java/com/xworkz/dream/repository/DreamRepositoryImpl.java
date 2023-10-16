package com.xworkz.dream.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
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
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.Synchronized;

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
	@Value("${sheets.followUpStatus}")
	private String followUpStatus;
	@Value("${sheets.emailAndNameRange}")
	private String emailAndNameRange;
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
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, range, body).setValueInputOption("USER_ENTERED")
				.execute();
		return true;
	}

	@Override
	@Cacheable(value = "emailData", key = "#spreadsheetId", unless = "#result == null")
	public ValueRange getEmails(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, emailRange).execute();
		return response;
	}

	@Override
	@Cacheable(value = "contactData", key = "#spreadsheetId", unless = "#result == null")
	public ValueRange getContactNumbers(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, contactNumberRange).execute();
		return response;
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
	@Cacheable(value = "sheetsData", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> readData(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
		return response.getValues();
	}

	@Override
	public UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange) throws IOException {
		return sheetsService.spreadsheets().values().update(spreadsheetId, range2, valueRange)
				.setValueInputOption("RAW").execute();
	}

	@Override
	public boolean saveToFollowUp(String spreadsheetId, List<Object> row) throws IOException {
		List<List<Object>> list = new ArrayList<List<Object>>();
		list.add(row);
		ValueRange body = new ValueRange().setValues(list);
		sheetsService.spreadsheets().values().append(spreadsheetId, followUpRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	@Override
	public boolean updateFollowUpStatus(String spreadsheetId, List<Object> statusData) throws IOException {
		List<List<Object>> list = new ArrayList<List<Object>>();
		list.add(statusData);
		ValueRange body = new ValueRange().setValues(list);
		sheetsService.spreadsheets().values().append(spreadsheetId, followUpStatus, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	@Override
	@Cacheable(value = "followUpDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getFollowUpDetails(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpRange).execute();
		return response.getValues();
	}

	@Override
	public boolean updateCurrentFollowUpStatus(String spreadsheetId, String currentFollowRange, List<Object> data)
			throws IOException {
		List<List<Object>> list = new ArrayList<List<Object>>();
		list.add(data);
		System.out.println(data.toString());
		ValueRange body = new ValueRange().setValues(list);
		sheetsService.spreadsheets().values().update(spreadsheetId, currentFollowRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;

	}

	@Override
	public ValueRange getStatusId(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpStatusIdRange).execute();
		return response;
	}

	@Override
	public List<List<Object>> getEmailsAndNames(String spreadsheetId, String value) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, emailAndNameRange).execute();

		return response.getValues();
	}

	@Override
	@Cacheable(value = "followUpStatusDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getFollowUpStatusDetails(String spreadsheetId) throws IOException {

		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpStatus).execute();
		return response.getValues();
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
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(spreadsheetId, dateOfBirthDetailsRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	@Override
	public UpdateValuesResponse updateFollow(String spreadsheetId, String range2, ValueRange valueRange)
			throws IOException {
		return sheetsService.spreadsheets().values().update(spreadsheetId, range2, valueRange)
				.setValueInputOption("RAW").execute();
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
	@CacheEvict(value = { "sheetsData", "emailData", "contactData", "followUpStatusDetails",
			"followUpDetails" }, allEntries = true)
	public void evictSheetsDataCaches() {
		// This method will be scheduled to run every 12 hours
		// and will evict all entries in the specified caches
	}

	@Override
	@CacheEvict(value = { "sheetsData", "emailData", "contactData", "followUpStatusDetails",
			"followUpDetails" }, allEntries = true)
	public void evictAllCachesOnTraineeDetails() {
		// will evict all entries in the specified caches

	}

	@Override
	@Cacheable(value = "followUpDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getFollowUpDetailsByid(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpRange).execute();
		return response.getValues();
	}
	
	@Override
	//@CachePut(value = "birthadayDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getBirthadayDetails(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, dateOfBirthDetailsRange).execute();
		return response.getValues();
	}

	@Override
	public List<List<Object>> getFollowupStatusByDate(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpRange).execute();
		System.out.println(response.getValues());
		return response.getValues();
	}

}
