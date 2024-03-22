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
public class BirthadayRepositoryImpl implements BirthadayRepository {

	private Sheets sheetsService;
	@Autowired
	private SheetPropertyDto sheetPropertyDto;
	@Autowired
	private SheetSaveOpration saveOperation;

	private static final Logger log = LoggerFactory.getLogger(BirthadayRepositoryImpl.class);

	@PostConstruct
	public void setSheetsService() {
		try {
			sheetsService = saveOperation.ConnsetSheetService();
		} catch (Exception e) {
			log.error("Exception while connecting to sheet,{}", e.getMessage());
		}
	}

	@Override
	public boolean saveBirthDayDetails(List<Object> row) {
		try {
			ValueRange value = sheetsService.spreadsheets().values()
					.get(sheetPropertyDto.getSheetId(), sheetPropertyDto.getDateOfBirthDetailsRange()).execute();
			if (value.getValues() != null && value.getValues().size() >= 1) {
				return saveOperation.saveDetilesWithDataSize(row, sheetPropertyDto.getDateOfBirthDetailsRange());
			} else {
				return saveOperation.saveDetilesWithoutSize(row, sheetPropertyDto.getDateOfBirthDetailsRange());
			}
		} catch (IOException e) {
			log.error("Exception while saving birthday details to sheet,{}", e.getMessage());
			return false;
		}
	}

	@Override
	@Cacheable(value = "getListOfBirthDayDetails", key = "'listOfBirthDayDetails'")
	public List<List<Object>> getBirthadayDetails(String spreadsheetId) {
		try {
			return sheetsService.spreadsheets().values()
					.get(sheetPropertyDto.getSheetId(), sheetPropertyDto.getDateOfBirthDetailsRange()).execute()
					.getValues();
		} catch (IOException e) {
			log.error("Exception while reading birthday emails,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public String updateDob(String rowRange, ValueRange valueRange) {
		try {
			UpdateValuesResponse response = sheetsService.spreadsheets().values()
					.update(sheetPropertyDto.getSheetId(), rowRange, valueRange)
					.setValueInputOption(RepositoryConstant.RAW.toString()).execute();
			if (response != null) {
				return "updated";
			}
		} catch (IOException e) {
			log.error("Exception in UpdateDOB Repo,{}", e);
		}
		return null;
	}

	@Override
	@Cacheable(value = "getListOfBirthDayEmail", key = "'listOfBirthDayEmail'")
	public List<List<Object>> getBirthadayEmailList() {
		try {
			return sheetsService.spreadsheets().values()
					.get(sheetPropertyDto.getSheetId(), sheetPropertyDto.getBirthDayEmailRange()).execute().getValues();
		} catch (IOException e) {
			log.error("Exception while reading birthday emails,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

}
