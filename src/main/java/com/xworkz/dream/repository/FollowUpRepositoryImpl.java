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
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.RepositoryConstant;
import com.xworkz.dream.dto.utils.SheetSaveOpration;

@Repository
public class FollowUpRepositoryImpl implements FollowUpRepository {

	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private Sheets sheetsService;
	@Value("${sheets.followUpStatus}")
	private String followUpStatus;
	@Value("${sheets.emailAndNameRange}")
	private String emailAndNameRange;
	@Value("${sheets.followUpRange}")
	private String followUpRange;
	@Value("${sheets.followUpEmailRange}")
	private String followUpEmailRange;
	@Value("${sheets.followUpStatusIdRange}")
	private String followUpStatusIdRange;
	@Autowired
	private SheetSaveOpration saveOpration;
	@Autowired
	private static final Logger log = LoggerFactory.getLogger(FollowUpRepositoryImpl.class);

	@PostConstruct
	private void setsheetsRepository() {
		sheetsService = saveOpration.ConnsetSheetService();
	}

	@Override
	public boolean saveToFollowUp(String spreadsheetId, List<Object> row) {
		List<List<Object>> list = new ArrayList<List<Object>>();
		List<Object> rowData = new ArrayList<>();
		rowData.add("");
		rowData.addAll(row.subList(1, row.size()));
		list.add(rowData);
		ValueRange body = new ValueRange().setValues(list);
		try {
			sheetsService.spreadsheets().values().append(spreadsheetId, followUpRange, body)
					.setValueInputOption(RepositoryConstant.USER_ENTERED.toString()).execute();
			return true;
		} catch (IOException e) {
			log.error("error getting data {} ", e);
			return false;
		}

	}

	@Override

	public boolean updateFollowUpStatus(String spreadsheetId, List<Object> statusData) {
		List<List<Object>> list = new ArrayList<List<Object>>();
		List<Object> rowData = new ArrayList<>();
		rowData.add("");
		rowData.addAll(statusData.subList(1, statusData.size()));
		list.add(rowData);
		ValueRange body = new ValueRange().setValues(list);
		try {
			sheetsService.spreadsheets().values().append(spreadsheetId, followUpStatus, body)
					.setValueInputOption(RepositoryConstant.USER_ENTERED.toString()).execute();
			return true;
		} catch (IOException e) {
			log.error("error update data {} ", e);
			return false;
		}
	}

	@Override
	@Cacheable(value="getFollowUpDetails",key="'listOfFollowUpDetails'")
	public List<List<Object>> getFollowUpDetails(String spreadsheetId) {
		try {

			log.info("FollowUp details retrieved successfully for spreadsheetId: {}", spreadsheetId);
			return sheetsService.spreadsheets().values().get(spreadsheetId, followUpRange).execute().getValues();
		} catch (IOException e) {
			log.error("error getting data {} ", e);
			return Collections.emptyList();
		}
	}

	@Override
	public boolean updateCurrentFollowUpStatus(String spreadsheetId, String currentFollowRange, List<Object> data) {
		List<List<Object>> list = new ArrayList<List<Object>>();
		list.add(data);
		ValueRange body = new ValueRange().setValues(list);
		try {
			sheetsService.spreadsheets().values().update(spreadsheetId, currentFollowRange, body)
					.setValueInputOption(RepositoryConstant.USER_ENTERED.toString()).execute();
			return true;
		} catch (IOException e) {
			log.error("error updating data {} ", e);
			return false;
		}
	}

	@Override
	@Cacheable(value = "getEmailList", key="'followUpEmailList'")
	public ValueRange getEmailList(String spreadsheetId) {
		try {
			return sheetsService.spreadsheets().values().get(spreadsheetId, followUpEmailRange).execute();
		} catch (IOException e) {
			log.error("error getting data {} ", e);
			return null;
		}
	}

	@Override
	//@Cacheable(value="getFollowUpStatusDetails",key="'followupstatusdetails'")
	public List<List<Object>> getFollowUpStatusDetails(String spreadsheetId) {
		log.info("FollowUp Status Details retrieved successfully for spreadsheetId: {}", spreadsheetId);
		try {
			return sheetsService.spreadsheets().values().get(spreadsheetId, followUpStatus).execute().getValues();
		} catch (IOException e) {
			log.error("error getting data {} ", e);
			return Collections.emptyList();
		}
	}
 

	@Override
	public UpdateValuesResponse updateFollow(String spreadsheetId, String range2, ValueRange valueRange) {
		log.info("FollowUp updated successfully for spreadsheetId: {}", spreadsheetId);
		try {
			return sheetsService.spreadsheets().values().update(spreadsheetId, range2, valueRange)
					.setValueInputOption(RepositoryConstant.RAW.toString()).execute();
		} catch (IOException e) {
			log.error("error updating data {} ", e);
			return null;
		}
	}

}
