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
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Repository
public class ClientRepositoryImpl implements ClientRepository {

	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private Sheets sheetsService;

	@Value("${sheets.clientInformationRange}")
	private String clientInformationRange;

	@Value("${login.sheetId}")
	public String sheetId;
	@Autowired
	private ResourceLoader resourceLoader;
	private static final Logger log = LoggerFactory.getLogger(ClientRepositoryImpl.class);

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
	public boolean writeClientInformation(List<Object> row) throws IOException {

		log.info("client repository running data is :{}", row);
		List<List<Object>> values = new ArrayList<>();
		// Add an empty string as a placeholder for the A column
		List<Object> rowData = new ArrayList<>();
		rowData.add(""); // Placeholder for A column
		rowData.addAll(row.subList(1, row.size())); // Start from the second element (B column)
		values.add(rowData);
		ValueRange body = new ValueRange().setValues(values);
		sheetsService.spreadsheets().values().append(sheetId, clientInformationRange, body)
				.setValueInputOption("USER_ENTERED").execute();
		return true;
	}

	@Override
	@Cacheable(value = "clientInformation", key = "#root.target.sheetId", unless = "#result == null")
	public List<List<Object>> readData() throws IOException {
		log.info(" client repository, reading client information ");
		ValueRange response = sheetsService.spreadsheets().values().get(sheetId, clientInformationRange).execute();
		return response.getValues();
	}
}
