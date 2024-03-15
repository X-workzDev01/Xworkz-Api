package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.RepositoryConstant;
import com.xworkz.dream.dto.SheetPropertyDto;
import com.xworkz.dream.dto.utils.SheetSaveOpration;

@Repository
public class BatchRepositoryImpl implements BatchRepository {

	private Sheets sheetsService;
	@Autowired
	private SheetPropertyDto sheetPropertyDto;
	@Autowired
	private SheetSaveOpration saveOperation;

	private static final Logger log = LoggerFactory.getLogger(BatchRepositoryImpl.class);

	@PostConstruct
	public void setSheetsService() {
		try {
			sheetsService = saveOperation.ConnsetSheetService();
		} catch (Exception e) {
			log.error("Exception while connecting to sheet,{}", e.getMessage());
		}
	}

	@Override
	public boolean saveBatchDetails(String spreadsheetId, List<Object> row) {
		try {
			ValueRange value = sheetsService.spreadsheets().values()
					.get(sheetPropertyDto.getSheetId(), sheetPropertyDto.getBatchDetailsRange()).execute();
			if (value.getValues() != null && value.getValues().size() >= 1) {
				return saveOperation.saveDetilesWithDataSize(row, sheetPropertyDto.getBatchDetailsRange());
			} else {
				return saveOperation.saveDetilesWithoutSize(row, sheetPropertyDto.getBatchDetailsRange());
			}
		} catch (IOException e) {
			log.error("Exception while saving birthday details to sheet,{}", e.getMessage());
			return false;
		}
	}

	@Override
	@Cacheable(value = "batchDetails", key = "'listOfBatch'")
	public List<List<Object>> getCourseDetails(String spreadsheetId) {
		try {
			log.debug("Course details retrieved successfully for spreadsheetId: {}", spreadsheetId);
			return sheetsService.spreadsheets().values()
					.get(sheetPropertyDto.getSheetId(), sheetPropertyDto.getBatchDetailsRange()).execute().getValues();

		} catch (Exception e) {
			log.error("Exception in getCourse details,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public UpdateValuesResponse updateBatchDetails(String spreadsheetId, String range2, ValueRange valueRange) {
		log.info("Batch details updated successfully for spreadsheetId: {}", spreadsheetId);
		try {
			return sheetsService.spreadsheets().values().update(sheetPropertyDto.getSheetId(), range2, valueRange)
					.setValueInputOption(RepositoryConstant.RAW.toString()).execute();
		} catch (IOException e) {
			log.error("exception in update batch details:{}", e.getMessage());
		}
		return null;
	}

	@Override
	@Cacheable(value = "getCourseNameList", key = "'courseName")
	public ValueRange getCourseNameList(String spreadsheetId) {
		log.debug("Course name list retrieved successfully for spreadsheetId: {}", spreadsheetId);
		ValueRange response = null;
		try {
			response = sheetsService.spreadsheets().values()
					.get(sheetPropertyDto.getSheetId(), sheetPropertyDto.getBatchDetailsCourseNameRange()).execute();
		} catch (IOException e) {
			log.error("exception in getcourse name list,{}", e.getMessage());
			return response;

		}
		return response;
	}

}
