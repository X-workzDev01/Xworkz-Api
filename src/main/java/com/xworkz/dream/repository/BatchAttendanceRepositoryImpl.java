package com.xworkz.dream.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.xworkz.dream.dto.utils.SheetSaveOpration;

@Repository
public class BatchAttendanceRepositoryImpl implements BatchAttendanceRepository {

	private Sheets sheetsService;
	@Value("${login.sheetId}")
	private String spreadSheetId;
	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	@Autowired 
	private ResourceLoader resourceLoader;
	@Autowired
	private SheetSaveOpration saveOpration;
	private static final Logger log = LoggerFactory.getLogger(AttendanceRepositoryImpl.class);

	@PostConstruct
	private void setSheetsService() throws Exception {

		Resource resource = resourceLoader.getResource(credentialsPath);
		File file = resource.getFile();

		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(file)).createScoped(SCOPES);

		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
		sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
				requestInitializer).setApplicationName(applicationName).build();
		log.info("Sheets service setup complete.");
	}

	@Override
	public Boolean batchAttendance(List<Object> row, String range) throws IOException {
		log.info("Running BatchAttendance repository  {}", row);

		ValueRange value = sheetsService.spreadsheets().values().get(spreadSheetId, range).execute();
		if (value.getValues() != null && value.getValues().size() >= 1) {
			log.info("Fees register sucessfully");
			return saveOpration.saveDetilesWithDataSize(row, range);

		} else {
			log.info("Fees register sucessfully");
			return saveOpration.saveDetilesWithoutSize(row, range);
		}

	}

	@Override
	// @Cacheable(value = "batchAttendanceData", key = "'listOfBatchAttendance'")
	public List<List<Object>> getBatchAttendanceData(String range) {
		log.info("Getting data from sheet...");
		ValueRange response;
		try {
			response = sheetsService.spreadsheets().values().get(spreadSheetId, range).execute();
			log.info("Data retrieved successfully.");
			return response.getValues();
		} catch (IOException e) {
			log.error("Read Batchattendance Data not working : {} ", e.getMessage());
		}
		return null;

	}

}
