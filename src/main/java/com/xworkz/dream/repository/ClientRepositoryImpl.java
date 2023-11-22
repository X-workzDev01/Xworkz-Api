package com.xworkz.dream.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.wrapper.ClientWrapper;

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
	private ClientWrapper clientWrapper;

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
		List<List<Object>> values = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		ValueRange valueRange = sheetsService.spreadsheets().values().get(sheetId, clientInformationRange).execute();
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
		return true;
	}

	@Override
	//@Cacheable(value = "clientInformation", key = "'ListOfClientDto'", unless = "#result == null")
	public List<ClientDto> readData() throws IOException {
		log.info(" client repository, reading client information ");
		
		List<List<Object>> values = sheetsService.spreadsheets().values().get(sheetId, clientInformationReadRange)
				.execute().getValues();
		if (values != null) {
			List<ClientDto> ListOfClientDto = values.stream()
					.map(clientWrapper::listToClientDto)
					.sorted(Comparator.comparing(ClientDto::getId, Comparator.reverseOrder()))
					.collect(Collectors.toList());
			return ListOfClientDto;
		} else {
			return null;
		}

	}
	@Override
	public boolean checkCompanyName(String companyName) throws IOException {
		boolean find = sheetsService.spreadsheets().values().get(sheetId, clientInformationReadRange).execute()
				.getValues().stream().map(clientWrapper::listToClientDto)
				.anyMatch(clientDto -> companyName.equals(clientDto.getCompanyName()));
		return find;
	}
	
	@Override
	public ClientDto getClientDtoByCompnayName(String companyName) throws IOException {
		ClientDto  dto= sheetsService.spreadsheets().values().get(sheetId, clientInformationReadRange).execute()
				.getValues().stream().map(clientWrapper::listToClientDto).filter(ClientDto->companyName.equalsIgnoreCase(ClientDto.getCompanyName())).findFirst().orElse(null);
		return dto;
	}

	@Override
	public ClientDto getClientDtoById(int companyId) throws IOException {
		ClientDto  dto= sheetsService.spreadsheets().values().get(sheetId, clientInformationReadRange).execute()
				.getValues().stream().map(clientWrapper::listToClientDto).filter(ClientDto->companyId==ClientDto.getId()).findFirst().orElse(null);
		return dto;
	}
}