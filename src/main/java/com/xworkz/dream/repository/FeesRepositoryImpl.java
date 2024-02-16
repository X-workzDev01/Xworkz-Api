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
public class FeesRepositoryImpl implements FeesRepository {
	@Value("${sheets.feesRegister}")
	private String feesRegisterRange;
	@Value("${login.sheetId}")
	private String spreadSheetId;
	private Sheets sheetsRepository;
	@Autowired
	private SheetSaveOpration saveOpration;
	private Logger log = LoggerFactory.getLogger(FeesRepositoryImpl.class);

	@PostConstruct
	private void setsheetsRepository() {
		try {
			sheetsRepository = saveOpration.ConnsetSheetService();
		} catch (Exception e) {
			log.error("Sheet Connection is Failed  {} ", e);
		}
	}

	@Override
	public boolean writeFeesDetiles(List<Object> list) {
		try {
			ValueRange value = sheetsRepository.spreadsheets().values().get(spreadSheetId, feesRegisterRange).execute();

			if (value.getValues() != null && value.getValues().size() >= 1) {
				return saveOpration.saveDetilesWithDataSize(list, feesRegisterRange);

			} else {
				return saveOpration.saveDetilesWithoutSize(list, feesRegisterRange);
			}
		} catch (IOException e) {
			log.error("error fetching data  {}  ", e);
			return false;
		}

	}

	@Override
	@Cacheable(value = "getFeesDetils", key = "'allDetils'")
	public List<List<Object>> getAllFeesDetiles(String getFeesDetilesRange) {
		try {
			return sheetsRepository.spreadsheets().values().get(spreadSheetId, getFeesDetilesRange).execute()
					.getValues();
		} catch (IOException e) {
			log.error("error fetching data  {}  ", e);
			return Collections.emptyList();
		}
	}

	@Override
	@Cacheable(value = "getFolllowUpdata", key = "'feesfollowUpData'")
	public List<List<Object>> getFeesDetilesByemailInFollowup(String getFeesDetilesfollowupRange) {
		try {
			return sheetsRepository.spreadsheets().values().get(spreadSheetId, getFeesDetilesfollowupRange).execute()
					.getValues();
		} catch (IOException e) {
			log.error("error fetching data  {}  ", e);
			return Collections.emptyList();
		}
	}

	@Override
	public String updateFeesDetiles(String getFeesDetilesfollowupRange, List<Object> list) {
		ValueRange body = saveOpration.updateDetilesToSheet(list);
		try {
			return sheetsRepository.spreadsheets().values().update(spreadSheetId, getFeesDetilesfollowupRange, body)
					.setValueInputOption("RAW").execute().setSpreadsheetId(spreadSheetId).getUpdatedRange();
		} catch (IOException e) {
			log.error("Error updating data {}     ", e);
			return "data Update Error";
		}
	}

	@Override
	public boolean updateDetilesToFollowUp(String followup, List<Object> list) {
		log.info("update fees followUp detiles form the sheet   " + list);

		try {
			ValueRange value = sheetsRepository.spreadsheets().values().get(spreadSheetId, followup).execute();

			if (value.getValues() != null && value.getValues().size() >= 1) {
				return saveOpration.saveDetilesWithDataSize(list, followup);

			} else {
				return saveOpration.saveDetilesWithoutSize(list, followup);
			}
		} catch (IOException e) {
			log.error("error update data  {}  ", e);
			return false;
		}
	}

	@Override
	@Cacheable(value = "getFeesEmail", key = "'email'")
	public List<List<Object>> getEmailList(String feesEmailRange) {
		try {
			return sheetsRepository.spreadsheets().values().get(spreadSheetId, feesEmailRange).execute().getValues();
		} catch (IOException e) {
			log.error("error fetching data  {}  ", e);
			return Collections.emptyList();
		}

	}
}
