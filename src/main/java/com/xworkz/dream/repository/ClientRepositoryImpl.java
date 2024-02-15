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
	@Value("${sheets.clientInformationReadRange}")
	private String clientInformationReadRange;
	@Value("${login.sheetId}")
	public String sheetId;

	@Autowired

	private ResourceLoader resourceLoader;
	private static final Logger log = LoggerFactory.getLogger(ClientRepositoryImpl.class);

	@Override
	@PostConstruct
	public void setSheetsService() {

		Resource resource = resourceLoader.getResource(credentialsPath);
		File file;
		try {
			file = resource.getFile();

			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(file))
					.createScoped(SCOPES);

			HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
			sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
					requestInitializer).setApplicationName(applicationName).build();
		} catch (Exception e) {
			log.error("Exception in setSheetService ClientRepo,{}", e.getMessage());
		}
	}

	@Override
	public boolean writeClientInformation(List<Object> row) {
		List<List<Object>> values = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		ValueRange valueRange;
		try {
			valueRange = sheetsService.spreadsheets().values().get(sheetId, clientInformationRange).execute();
			if (valueRange.getValues() != null && valueRange.getValues().size() >= 1) {
				log.debug("if sheet doesn't contain any data:{}", valueRange);
				rowData.add("");
				rowData.addAll(row.subList(1, row.size()));
				values.add(rowData);
				ValueRange body = new ValueRange().setValues(values);
				sheetsService.spreadsheets().values().append(sheetId, clientInformationRange, body)
						.setValueInputOption("USER_ENTERED").execute();
			} else {
				log.debug("if sheet doesn't contain any data:{}", valueRange);
				rowData.addAll(row.subList(1, row.size()));
				values.add(rowData);
				ValueRange body = new ValueRange().setValues(values);
				sheetsService.spreadsheets().values().append(sheetId, clientInformationRange, body)
						.setValueInputOption("USER_ENTERED").execute();
			}
		} catch (Exception e) {
			log.error("Exception in write Client repo,{}", e.getMessage());
		}
		return true;
	}

	@Cacheable(value = "clientInformation", key = "'ListOfClientDto'")
	public List<List<Object>> readData() {
		log.info(" client repository, reading client information ");
		ValueRange valueRange = null;
		try {
			valueRange = sheetsService.spreadsheets().values().get(sheetId, clientInformationReadRange).execute();
		} catch (IOException e) {
			log.error("Exception in read ClientRepo,{}", e.getMessage());
		}
		List<List<Object>> values = valueRange.getValues();
		return values;
	}

	@Override
	public UpdateValuesResponse updateclientInfo(String range, ValueRange valueRange) {
		log.info("updating Coompany Details to the sheet, {}", range);
		UpdateValuesResponse response = null;
		try {
			response = sheetsService.spreadsheets().values().update(sheetId, range, valueRange)
					.setValueInputOption("RAW").execute();
		} catch (IOException e) {
			log.error("Exception in updateclientInfo ClientRepo,{}", e.getMessage());
		}
		return response;
	}

}
