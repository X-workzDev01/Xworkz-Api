package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.RepositoryConstant;
import com.xworkz.dream.dto.utils.SheetSaveOpration;

@Repository
public class RegisterRepositoryImpl implements RegisterRepository {

	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
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
	private SheetSaveOpration saveOperation;
	private static final Logger log = LoggerFactory.getLogger(RegisterRepositoryImpl.class);

	@PostConstruct
	public void setSheetsService() {
		try {
			sheetsService = saveOperation.ConnsetSheetService();
		} catch (Exception e) {
			log.error("Exception while connecting to sheet,{}", e.getMessage());
		}
	}

	@Override
	public boolean writeData(String spreadsheetId, List<Object> row) {
		try {
			ValueRange value = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
			if (value.getValues() != null && value.getValues().size() >= 1) {
				return saveOperation.saveDetilesWithDataSize(row, range);
			} else {
				return saveOperation.saveDetilesWithoutSize(row, range);
			}
		} catch (IOException e) {
			log.error("Exception while saving data to sheet,{}", e.getMessage());
			return false;
		}
	}

	@Override
	@Cacheable(value = "emailData", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getEmails(String spreadsheetId, String email) {
		try {
			return sheetsService.spreadsheets().values().get(spreadsheetId, emailRange).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in getEmails,{}", e.getMessage());
			return Collections.emptyList();
		}
	} 

	@Override
	@Cacheable(value = "contactData", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getContactNumbers(String spreadsheetId) {
		try {
			return sheetsService.spreadsheets().values().get(spreadsheetId, contactNumberRange).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in getContactNumber repository,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	@Cacheable(value = "sheetsData", key = "'listOfTraineeData'", unless = "#result == null")
	public List<List<Object>> readData(String spreadsheetId) {
		try {
			System.err.println(sheetsService.spreadsheets().values().get(spreadsheetId, range).execute().getValues());
			return sheetsService.spreadsheets().values().get(spreadsheetId, range).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in readData method, {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange) {
		try {
			return sheetsService.spreadsheets().values().update(spreadsheetId, range2, valueRange)
					.setValueInputOption(RepositoryConstant.RAW.toString()).execute();
		} catch (IOException e) {
			log.error("Exception in update method, {}", e.getMessage());
			return null;
		}

	} 

	@Override
	public List<List<Object>> getEmailsAndNames(String spreadsheetId, String value) {
		try {
			return sheetsService.spreadsheets().values().get(spreadsheetId, emailAndNameRange).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in getEmailsAndNames method,{}", e.getMessage());
			return Collections.emptyList();
		}

	}

	@Override
	@Cacheable(value = "alternativeNumber", key = "'listOfAlternativeContactNumbers'", unless = "#result == null")
	public List<List<Object>> getAlternativeNumber(String spreadsheetId) {
		try {
			return sheetsService.spreadsheets().values().get(spreadsheetId, alternativeNumberRange).execute()
					.getValues();
		} catch (IOException e) {
			log.error("Exception in getAlternativeNumber method, {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	@Cacheable(value = "usnNumber", key = "'listOfUsnNumbers'", unless = "#result == null")
	public List<List<Object>> getUsnNumber(String spreadsheetId) {
		try {
			return sheetsService.spreadsheets().values().get(spreadsheetId, usnNumberRange).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in getUsnNumber method, {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	@Cacheable(value = "uniqueNumber", key = "'listofUniqueNumbers'")
	public List<List<Object>> getUniqueNumbers(String spreadsheetId) {
		try {
			return sheetsService.spreadsheets().values().get(spreadsheetId, uniqueNumberRange).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in getUniqueNumbers method, {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	
}
