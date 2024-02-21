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
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.utils.SheetSaveOpration;

@Repository
public class HrFollowUpRepositoryImpl implements HrFollowUpRepository {

	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	private Sheets sheetsService;
	@Value("${sheets.hrFollowUpInformationRange}")
	private String hrFollowUpInformationRange;
	@Value("${sheets.hrFollowUpInformationReadRange}")
	private String hrFollowUpInformationReadRange;
	@Value("${login.sheetId}")
	public String sheetId;
	@Autowired
	private SheetSaveOpration saveOperation;

	private static final Logger log = LoggerFactory.getLogger(HrFollowUpRepositoryImpl.class);

	@PostConstruct
	public void setSheetsService() {
		try {
			sheetsService = saveOperation.ConnsetSheetService();
		} catch (Exception e) {
			log.error("Exception while connecting to sheet,{}", e.getMessage());
		}
	}

	@Override
	public boolean saveHrFollowUpDetails(List<Object> row) {
		try {
			ValueRange value = sheetsService.spreadsheets().values().get(sheetId, hrFollowUpInformationRange).execute();
			if (value.getValues() != null && value.getValues().size() >= 1) {
				return saveOperation.saveDetilesWithDataSize(row, hrFollowUpInformationRange);
			} else {
				return saveOperation.saveDetilesWithoutSize(row, hrFollowUpInformationRange);
			}
		} catch (IOException e) {
			log.error("Exception while saving data to sheet,{}", e.getMessage());
			return false;
		}
	}

	@Override
	@Cacheable(value = "hrFollowUpDetails", key = "'hrFollowUp'")
	public List<List<Object>> readFollowUpDetailsById() {
		try {
			return sheetsService.spreadsheets().values().get(sheetId, hrFollowUpInformationReadRange).execute()
					.getValues();
		} catch (IOException e) {
			log.error("Exception in read ClientRepo,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

}
