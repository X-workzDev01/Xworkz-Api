package com.xworkz.dream.dto.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class SaveSheetOprationImpl implements SheetSaveOpration {
	Logger log = LoggerFactory.getLogger(SaveSheetOprationImpl.class);
	@Autowired
	private ResourceLoader resourceLoader;
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private Sheets sheetsService;
	@Value("${login.sheetId}")
	private String spreadSheetId;

	@Override
	public boolean saveDetilesWithoutSize(List<Object> list, String feesRegisterRange) {

		List<List<Object>> values = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		rowData.addAll(list.subList(1, list.size()));
		values.add(rowData);
		ValueRange body = new ValueRange().setValues(values);
		try {
			sheetsService.spreadsheets().values().append(spreadSheetId, feesRegisterRange, body)
					.setValueInputOption("USER_ENTERED").execute();
		} catch (IOException e) {
			log.error("error connection ", e);
		}
		log.debug("registering fees repository data list is : {}", body);
		return true;

	}

	@Override
	public Sheets ConnsetSheetService() {

		try {
			GoogleCredentials credentials;
			Resource resource = resourceLoader.getResource(credentialsPath);
			File file = resource.getFile();
			credentials = GoogleCredentials.fromStream(new FileInputStream(file)).createScoped(SCOPES);

			HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
			sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
					requestInitializer).setApplicationName(applicationName).build();
			return sheetsService;
		} catch (Exception e) {
			log.info("Error connection Sheet {} ", e);
			return null;
		}
	}

	@Override
	public boolean saveDetilesWithDataSize(List<Object> list, String feesRegisterRange) {
		List<List<Object>> values = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		rowData.add("");
		rowData.addAll(list.subList(1, list.size()));
		values.add(rowData);
		
		ValueRange body = new ValueRange().setValues(values);
		try {
			log.debug("registering fees repository data list is : {}", body);
			sheetsService.spreadsheets().values().append(spreadSheetId, feesRegisterRange, body)
					.setValueInputOption("USER_ENTERED").execute();
			return true;
		} catch (IOException e) {
			log.error("Error writing data {}   ", e);
			return false;

		}

	}

	@Override
	public ValueRange updateDetilesToSheet(List<Object> list) {
		List<List<Object>> values = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		rowData.add(null);
		rowData.addAll(list.subList(1, list.size()));
		values.add(rowData);

		ValueRange body = new ValueRange().setValues(values);
		return body;
	}

}
