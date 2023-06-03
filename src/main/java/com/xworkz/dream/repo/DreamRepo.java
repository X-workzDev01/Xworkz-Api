package com.xworkz.dream.repo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.wrapper.DreamWrapper;


@Repository
public class DreamRepo {
		
		@Value("${sheets.appName}")
	 	private String applicationName;
		@Value("${sheets.credentialsPath}")
		private String credentialsPath;
	    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	    private  Sheets sheetsService;
	    @Value("${sheets.range}")
	    private String range;
	    @Value("${sheets.emailRange}")
	    private String emailRange;
	    @Value("${sheets.contactNumberRange}")
	    private String contactNumberRange;
	    
	    
	   
	    @PostConstruct
		private void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException {
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
	                .createScoped(SCOPES);
			
			 HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
				        credentials);
			sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, requestInitializer)
		            .setApplicationName(applicationName)
		            .build();
		}
	    
	    public boolean writeData(String spreadsheetId,  TraineeDto dto) throws IOException {
	        List<List<Object>> values = new ArrayList<>();
	        List<Object> row = DreamWrapper.dtoToList(dto);
	        values.add(row);
	        ValueRange body = new ValueRange().setValues(values);
	        sheetsService.spreadsheets().values()
	                .append(spreadsheetId, range, body)
	                .setValueInputOption("USER_ENTERED")
	                .execute();
	        return true;
	    }

		public ValueRange getEmails(String spreadsheetId) throws IOException {
			ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, emailRange)
                    .execute();
					return response;
		}
		
		public ValueRange getContactNumbers(String spreadsheetId) throws IOException {
			ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, contactNumberRange)
                    .execute();
					return response;
		}
	    
	    
	    
	    
	
	
	

}
