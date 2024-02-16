package com.xworkz.dream.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
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
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
	@Value("${sheets.followUpRange}")
	private String followUpRange;
	@Value("${sheets.getFeesDetiles}")
	private String getFeesDetiles;
	private Sheets sheetsService;
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	@Autowired
	private ResourceLoader resourceLoader;

	private static final Logger log = LoggerFactory.getLogger(NotificationRepositoryImpl.class);

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
	public List<List<Object>> notification(String spreadsheetId) {
		try {
			return sheetsService.spreadsheets().values().get(spreadsheetId, followUpRange).execute().getValues();
		} catch (IOException e) {
			log.error("error fetching data list is empty {} ", e);
			return Collections.emptyList();
		}

	}
	@Override
	public List<List<Object>> feesNotification(String spreadsheetId) {

		log.info("Get all feesNotification detiles ");
		try {
			log.info("Fees Notification details retrieved successfully for spreadsheetId: {}", spreadsheetId);
			return sheetsService.spreadsheets().values().get(spreadsheetId, getFeesDetiles).execute().getValues();
			
		} catch (IOException e) {
		    log.error("Error Fees Data retrieved : {} ",e.getMessage());
		    return Collections.emptyList();
		}
		
	}

}
