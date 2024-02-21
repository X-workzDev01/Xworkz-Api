package com.xworkz.dream.repository;

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
import org.springframework.stereotype.Repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.RepositoryConstant;
import com.xworkz.dream.dto.utils.SheetSaveOpration;

@Repository
public class DreamRepositoryImpl implements DreamRepository {

	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private Sheets sheetsService;
	@Value("${sheets.dropdownRange}")
	private String dropdownRange;
	@Value("${sheets.idRange}")
	private String idRange;
	@Value("${sheets.loginInfoRange}")
	private String loginInfoRange;
	@Value("${login.sheetId}")
	private String sheetId;
	@Value("${sheets.clientDropDownRange}")
	private String clientDropDownRange;
	@Autowired
	private SheetSaveOpration saveOperation;

	private static final Logger log = LoggerFactory.getLogger(DreamRepositoryImpl.class);

	@PostConstruct
	public void setSheetsService() {
		try {
			sheetsService=saveOperation.ConnsetSheetService();
		} catch (Exception e) {
			log.error("Exception while connecting to sheet,{}",e.getMessage());
		}
	}

	@Override
	@Cacheable(value = "getDropdowns", key = "#spreadsheetId", unless = "#result == null")
	public List<List<Object>> getDropdown(String spreadsheetId) {
		try {
		return sheetsService.spreadsheets().values().get(spreadsheetId, dropdownRange).execute().getValues();
		} catch (IOException e) {
			log.error("Exception while reading dropdown :{}",e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public boolean updateLoginInfo(String spreadsheetId, List<Object> row) {
		List<List<Object>> values = new ArrayList<>();
		values.add(row);
		ValueRange body = new ValueRange().setValues(values);
		try {
			sheetsService.spreadsheets().values().append(spreadsheetId, loginInfoRange, body)
					.setValueInputOption(RepositoryConstant.USER_ENTERED.toString()).execute();
			return true;
		} catch (IOException e) {
			log.error("Exception while updating login info:{}",e.getMessage());
			return false;
		}
		

	}

	@Override
	@Cacheable(value="getClientDropDown",key="'listOfClientDropDown'", unless = "#result == null")
	public List<List<Object>> getClientDropDown() {
		try {
			return sheetsService.spreadsheets().values().get(sheetId, clientDropDownRange).execute().getValues();
		} catch (IOException e) {
			log.error("Exception while reading client dropdown:{}", e.getMessage());
			return Collections.emptyList();
		}
	}

}
