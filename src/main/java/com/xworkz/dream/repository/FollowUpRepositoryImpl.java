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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class FollowUpRepositoryImpl implements FollowUpRepository {

	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private Sheets sheetsService;
	@Value("${sheets.followUpStatus}")
	private String followUpStatus;
	@Value("${sheets.emailAndNameRange}")
	private String emailAndNameRange;
	@Value("${sheets.followUpRange}")
	private String followUpRange;
	@Value("${sheets.followUpEmailRange}")
	private String followUpEmailRange;
	@Value("${sheets.followUpStatusIdRange}")
	private String followUpStatusIdRange;

	@Autowired
	private ResourceLoader resourceLoader;
	private static final Logger log = LoggerFactory.getLogger(FollowUpRepositoryImpl.class);

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
	public boolean saveToFollowUp(String spreadsheetId, List<Object> row) throws IOException {
		log.info("FollowUp Registration Running Repository {} ", row);
		List<List<Object>> list = new ArrayList<List<Object>>();
		// Add an empty string as a placeholder for the A column
		List<Object> rowData = new ArrayList<>();
		rowData.add(""); // Placeholder for A column
		rowData.addAll(row.subList(1, row.size())); // Start from the second element (B column)
		list.add(rowData);
		ValueRange body = new ValueRange().setValues(list);
		sheetsService.spreadsheets().values().append(spreadsheetId, followUpRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		log.info("FollowUp Registration Successful for spreadsheetId: {}", spreadsheetId);
		return true;
	}

	@Override
	// @CachePut(value = "followUpDetails", key = "#spreadsheetId", unless =
	// "#result == null")
	public boolean updateFollowUpStatus(String spreadsheetId, List<Object> statusData) throws IOException {
		List<List<Object>> list = new ArrayList<List<Object>>();
		List<Object> rowData = new ArrayList<>();
		rowData.add(""); // Placeholder for A column
		rowData.addAll(statusData.subList(1, statusData.size())); // Start from the second element (B column)
		list.add(rowData);
		ValueRange body = new ValueRange().setValues(list);
		sheetsService.spreadsheets().values().append(spreadsheetId, followUpStatus, body)
				.setValueInputOption("USER_ENTERED").execute();
		log.info("FollowUp status updated successfully for spreadsheetId: {}", spreadsheetId);
		return true;
	}

	@Override
//	@Cacheable(value = "followUpDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getFollowUpDetails(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpRange).execute();
		log.info("FollowUp details retrieved successfully for spreadsheetId: {}", spreadsheetId);
		return response.getValues();
	}

	@Override
	// @CachePut(value = "followUpDetails", key = "#spreadsheetId", unless =
	// "#result == null")
	public boolean updateCurrentFollowUpStatus(String spreadsheetId, String currentFollowRange, List<Object> data)
			throws IOException {
		List<List<Object>> list = new ArrayList<List<Object>>();
		list.add(data);
		ValueRange body = new ValueRange().setValues(list);
		sheetsService.spreadsheets().values().update(spreadsheetId, currentFollowRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		log.info("Current FollowUp status updated successfully for spreadsheetId: {}", spreadsheetId);
		return true;
	}

	@Override
	public ValueRange getEmailList(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpEmailRange).execute();
		log.info("Email list retrieved successfully for spreadsheetId: {}", spreadsheetId);
		return response;
	}

	@Override
	public ValueRange getStatusId(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpStatusIdRange).execute();
		log.info("Status ID retrieved successfully for spreadsheetId: {}", spreadsheetId);
		return response;
	}

	@Override
	public List<List<Object>> getEmailsAndNames(String spreadsheetId, String value) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, emailAndNameRange).execute();
		log.info("Emails and Names retrieved successfully for spreadsheetId: {}", spreadsheetId);
		return response.getValues();
	}

	@Override
//	@Cacheable(value = "followUpStatusDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getFollowUpStatusDetails(String spreadsheetId) throws IOException {
		log.info("FollowUp Status Details retrieved successfully for spreadsheetId: {}", spreadsheetId);
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpStatus).execute();
		return response.getValues();
	}

	@Override
//	@Cacheable(value = "followUpDetails", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getFollowUpDetailsByid(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpRange).execute();
		log.info("FollowUp Details by ID retrieved successfully for spreadsheetId: {}", spreadsheetId);
		return response.getValues();
	}

	@Override
	public List<List<Object>> getFollowupStatusByDate(String spreadsheetId) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, followUpRange).execute();
		log.info("FollowUp Status by Date retrieved successfully for spreadsheetId: {}", spreadsheetId);
		return response.getValues();
	}

	@Override
	// @CachePut(value = "followUpDetails", key = "#spreadsheetId", unless =
	// "#result == null")
	public UpdateValuesResponse updateFollow(String spreadsheetId, String range2, ValueRange valueRange)
			throws IOException {
		log.info("FollowUp updated successfully for spreadsheetId: {}", spreadsheetId);
		return sheetsService.spreadsheets().values().update(spreadsheetId, range2, valueRange)
				.setValueInputOption("RAW").execute();
	}

	@Override
//	@CacheEvict(value = { "followUpStatusDetails" }, allEntries = true)
	public void evictFollowUpStatusDetails() {
		// This method will be scheduled to run every 12 hours
		// and will evict all entries in the specified caches
	}

}
