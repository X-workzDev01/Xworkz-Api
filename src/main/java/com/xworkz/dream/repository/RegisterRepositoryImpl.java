package com.xworkz.dream.repository;

import java.io.File;
import java.io.FileInputStream;
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
public class RegisterRepositoryImpl implements RegisterRepository {

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
	@Value("${sheets.emailAndNameRange}")
	private String emailAndNameRange;
	@Value("${sheets.alternativeNumberRange}")
	private String alternativeNumberRange;
	@Value("${sheets.usnNumberRange}")
	private String usnNumberRange;
	@Value("${sheets.uniqueNumberRange}")
	private String uniqueNumberRange;
	@Autowired
	private ResourceLoader resourceLoader;
	private static final Logger log = LoggerFactory.getLogger(RegisterRepositoryImpl.class);

	@Override
	@PostConstruct
	public void setSheetsService() {
		Resource resource = resourceLoader.getResource(credentialsPath);
		File file;
		try {
			file = resource.getFile();
			GoogleCredentials credentials;
			credentials = GoogleCredentials.fromStream(new FileInputStream(file)).createScoped(SCOPES);
			HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
			sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
					requestInitializer).setApplicationName(applicationName).build();
		} catch (GeneralSecurityException | IOException e) {
			log.error("Exception in setSheetsService repository,{}", e.getMessage());
		}
	}

	@Override
	public boolean writeData(String spreadsheetId, List<Object> row) {
		List<List<Object>> values = new ArrayList<>();
		// Add an empty string as a placeholder for the A column
		List<Object> rowData = new ArrayList<>();
		rowData.add(""); // Placeholder for A column
		rowData.addAll(row.subList(1, row.size())); // Start from the second element (B column)
		values.add(rowData);
		ValueRange body = new ValueRange().setValues(values);
		try {
			sheetsService.spreadsheets().values().append(spreadsheetId, range, body).setValueInputOption("USER_ENTERED")
					.execute();
		} catch (IOException e) {
			log.error("Exception in write repository:{}", e.getMessage());
		}
		log.info("Data written successfully to spreadsheetId: {}", spreadsheetId);
		return true;
	}

	@Override
	@Cacheable(value = "emailData", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getEmails(String spreadsheetId, String email) {
		log.info("Reading email data from sheet for spreadsheetId: {}", spreadsheetId);
		ValueRange emailValue = null;
		try {
			emailValue = sheetsService.spreadsheets().values().get(spreadsheetId, emailRange).execute();
		} catch (IOException e) {
			log.error("Exception in getEmails,{}", e.getMessage());
		}
		return emailValue.getValues();
	}

	@Override
	@Cacheable(value = "contactData", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getContactNumbers(String spreadsheetId) {
		log.info("Reading contact numbers from sheet for spreadsheetId: {}", spreadsheetId);
		ValueRange response = null;
		try {
			response = sheetsService.spreadsheets().values().get(spreadsheetId, contactNumberRange).execute();
		} catch (IOException e) {
			log.error("Exception in getContactNumber repository,{}", e.getMessage());
		}
		return response.getValues();
	}

	@Override
	@Cacheable(value = "sheetsData", key = "'listOfTraineeData'", unless = "#result == null")
	public List<List<Object>> readData(String spreadsheetId) {
		log.info("Reading Trainee data from sheet for spreadsheetId: {}", spreadsheetId);
		ValueRange response = null;
		try {
			response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
		} catch (IOException e) {
			log.error("Exception in readData method, {}", e.getMessage());
		}
		List<List<Object>> data = response.getValues();
		return data;
	}

	@Override
	public UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange) {
		log.info("Data updated successfully in spreadsheetId: {}", spreadsheetId);
		UpdateValuesResponse response = null;
		try {
			response = sheetsService.spreadsheets().values().update(spreadsheetId, range2, valueRange)
					.setValueInputOption("RAW").execute();
		} catch (IOException e) {
			log.error("Exception in update method, {}", e.getMessage());
		}
		return response;
	}

	@Override
	public List<List<Object>> getEmailsAndNames(String spreadsheetId, String value) {
		log.info("Reading emails and names from sheet for spreadsheetId: {}", spreadsheetId);
		ValueRange response = null;
		try {
			response = sheetsService.spreadsheets().values().get(spreadsheetId, emailAndNameRange).execute();
		} catch (IOException e) {
			log.error("Exception in getEmailsAndNames method,{}", e.getMessage());
		}
		return response.getValues();
	}

	@Override
	@Cacheable(value = "alternativeNumber", key = "'listOfAlternativeContactNumbers'", unless = "#result == null")
	public List<List<Object>> getAlternativeNumber(String spreadsheetId) {
		log.info("Reading Alternative contact number from sheet");
		ValueRange response = null;
		try {
			response = sheetsService.spreadsheets().values().get(spreadsheetId, alternativeNumberRange).execute();
		} catch (IOException e) {
			log.error("Exception in getAlternativeNumber method, {}", e.getMessage());
		}
		return response.getValues();
	}

	@Override
	@Cacheable(value = "usnNumber", key = "'listOfUsnNumbers'", unless = "#result == null")
	public List<List<Object>> getUsnNumber(String spreadsheetId) {
		log.info("Reading Usn Number from sheet");
		ValueRange response = null;
		try {
			response = sheetsService.spreadsheets().values().get(spreadsheetId, usnNumberRange).execute();
		} catch (IOException e) {
			log.error("Exception in getUsnNumber method, {}", e.getMessage());
		}
		return response.getValues();
	}

	@Override
	@Cacheable(value = "uniqueNumber", key = "'listofUniqueNumbers'")
	public List<List<Object>> getUniqueNumbers(String spreadsheetId) {
		log.info("Reading Unique numbers from sheet");
		ValueRange response = null;
		try {
			response = sheetsService.spreadsheets().values().get(spreadsheetId, uniqueNumberRange).execute();
		} catch (IOException e) {
			log.error("Exception in getUniqueNumbers method, {}", e.getMessage());
		}
		return response.getValues();

	}
}
