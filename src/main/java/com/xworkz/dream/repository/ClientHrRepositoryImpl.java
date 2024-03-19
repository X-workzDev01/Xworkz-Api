
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
import com.xworkz.dream.dto.PropertiesDto;
import com.xworkz.dream.dto.utils.SheetSaveOpration;


@Repository
public class ClientHrRepositoryImpl implements ClientHrRepository {

	private Sheets sheetsService;
	@Autowired
	private SheetSaveOpration saveOperation;
	@Autowired
	private PropertiesDto propertiesDto;
	private static final Logger log = LoggerFactory.getLogger(ClientHrRepositoryImpl.class);

	@PostConstruct
	public void setSheetsService() {
		try {
			sheetsService = saveOperation.ConnsetSheetService();
		} catch (Exception e) {
			log.error("Exception while connecting to sheet,{}", e.getMessage());
		}

	}

	@Override
	public boolean saveClientHrInformation(List<Object> row) {
		try {
			ValueRange value = sheetsService.spreadsheets().values()
					.get(propertiesDto.getSheetId(), propertiesDto.getClientHrInformationRange()).execute();
			if (value.getValues() != null && value.getValues().size() >= 1) {
				return saveOperation.saveDetilesWithDataSize(row, propertiesDto.getClientHrInformationRange());
			} else {
				return saveOperation.saveDetilesWithoutSize(row, propertiesDto.getClientHrInformationRange());
			}
		} catch (IOException e) {
			log.error("Exception while saving data to sheet,{}", e.getMessage());
			return false;
		}

	}

	@Override
	@Cacheable(value = "hrDetails", key = "'listofHRDetails'")
	public List<List<Object>> readData() {
		try {
			return sheetsService.spreadsheets().values()
					.get(propertiesDto.getSheetId(), propertiesDto.getClientHrInformationReadRange()).execute()
					.getValues();
		} catch (IOException e) {
			log.error("Exception in readData repository,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public UpdateValuesResponse updateHrDetails(String range, ValueRange valueRange) {
		log.info("updating the HR details ,{}", valueRange);
		try {
			return sheetsService.spreadsheets().values().update(propertiesDto.getSheetId(), range, valueRange)
					.setValueInputOption(RepositoryConstant.RAW.toString()).execute();
		} catch (IOException e) {
			log.error("Exception in updateHrDetails repository,{}", e.getMessage());
		}
		return null;
	}

	@Override
	@Cacheable(value="getListOfHrEmail",key="'listOfHrEmail'")
	public List<List<Object>> getListOfHrEmails() {
		try {
			return sheetsService.spreadsheets().values()
					.get(propertiesDto.getSheetId(), propertiesDto.getClientHrEmailRange()).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in readData repository,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	@Cacheable(value="getListOfContactNumber",key="'listOfHrContactNumber'")
	public List<List<Object>> getListOfHrContactNumber() {
		try {
			return sheetsService.spreadsheets().values()
					.get(propertiesDto.getSheetId(), propertiesDto.getClientHrContactNumberRange()).execute()
					.getValues();
		} catch (IOException e) {
			log.error("Exception in readData repository,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

}
