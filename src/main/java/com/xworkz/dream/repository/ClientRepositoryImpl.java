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
public class ClientRepositoryImpl implements ClientRepository {

	private Sheets sheetsService;
	@Autowired
	private SheetSaveOpration saveOperation;
	@Autowired
	private PropertiesDto propertiesDto;
	private static final Logger log = LoggerFactory.getLogger(ClientRepositoryImpl.class);


	@PostConstruct
	public void setSheetsService() {
		try {
			sheetsService = saveOperation.ConnsetSheetService();
		} catch (Exception e) {
			log.error("Exception while connecting to sheet,{}", e.getMessage());
		}
	}

	@Override
	public boolean writeClientInformation(List<Object> row) {
		try {
			ValueRange value = sheetsService.spreadsheets().values()
					.get(propertiesDto.getSheetId(), propertiesDto.getClientInformationRange()).execute();
			if (value.getValues() != null && value.getValues().size() >= 1) {
				return saveOperation.saveDetilesWithDataSize(row, propertiesDto.getClientInformationRange());
			} else {
				return saveOperation.saveDetilesWithoutSize(row, propertiesDto.getClientInformationRange());
			}
		} catch (IOException e) {
			log.error("Exception while saving data to sheet,{}", e.getMessage());
			return false;
		}
	}

	//@Cacheable(value = "clientInformation", key = "'listOfClientDto'")
	public List<List<Object>> readData() {
		log.info(" client repository, reading client information,{}", propertiesDto.getClientInformationReadRange());
		try {
			return sheetsService.spreadsheets().values()
					.get(propertiesDto.getSheetId(), propertiesDto.getClientInformationReadRange()).execute()
					.getValues();
		} catch (IOException e) {
			log.error("Exception in read ClientRepo,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public UpdateValuesResponse updateclientInfo(String range, ValueRange valueRange) {
		log.info("updating Coompany Details to the sheet, {}", valueRange);
		try {
			return sheetsService.spreadsheets().values().update(propertiesDto.getSheetId(), range, valueRange)
					.setValueInputOption(RepositoryConstant.RAW.toString()).execute();
		} catch (IOException e) {
			log.error("Exception in updateclientInfo ClientRepo,{}", e.getMessage());
			return null;
		}
	}

	@Override
	@Cacheable(value="getClientEmail",key="'listOfClientEmail'")
	public List<List<Object>> getClientListOfEmail() {
		try {
			return sheetsService.spreadsheets().values()
					.get(propertiesDto.getSheetId(), propertiesDto.getClientEmailRange()).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in read ClientRepo,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	@Cacheable(value="getClientContactNumbers",key="'listOfClientContactNumber'")
	public List<List<Object>> getClientContactNumber() {
		try {
			return sheetsService.spreadsheets().values()
					.get(propertiesDto.getSheetId(), propertiesDto.getClientContactNumberRange()).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in read ClientRepo,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	@Cacheable(value="getClientWebsite",key="'listOfClientWebsite'")
	public List<List<Object>> getClientWebsite() {
		try {
			return sheetsService.spreadsheets().values()
					.get(propertiesDto.getSheetId(), propertiesDto.getClientWebsiteRange()).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in read ClientRepo,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	@Cacheable(value="getClientName",key="'listOfClientName'")
	public List<List<Object>> getClientName() {
		try {
			return sheetsService.spreadsheets().values()
					.get(propertiesDto.getSheetId(), propertiesDto.getClientNameRange()).execute().getValues();
		} catch (IOException e) {
			log.error("Exception in read ClientRepo,{}", e.getMessage());
			return Collections.emptyList();
		}
	}

}
