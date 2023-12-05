package com.xworkz.dream.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	private void setsheetsRepository() throws IOException, FileNotFoundException, GeneralSecurityException {
		sheetsRepository = saveOpration.ConnsetSheetService();
	}

	@Override
	public boolean writeFeesDetiles(List<Object> list) throws IOException {
		log.info("Running fees repository  {}", list);

		ValueRange value = sheetsRepository.spreadsheets().values().get(spreadSheetId, feesRegisterRange).execute();
		if (value.getValues() != null && value.getValues().size() >= 1) {
			log.info("Fees register sucessfully");
			return saveOpration.saveDetilesWithDataSize(list, feesRegisterRange);

		} else {
			log.info("Fees register sucessfully");
			return saveOpration.saveDetilesWithoutSize(list, feesRegisterRange);
		}

	}

	@Override
	public List<List<Object>> getAllFeesDetiles(String getFeesDetilesRange) throws IOException {
		log.info("get fees detiles form the sheet");
		return sheetsRepository.spreadsheets().values().get(spreadSheetId, getFeesDetilesRange).execute().getValues();
	}

	@Override
	
	public List<List<Object>> getFeesDetilesByemailInFollowup(String getFeesDetilesfollowupRange) throws IOException {
		log.info("get fees followUp detiles form the sheet");
		return sheetsRepository.spreadsheets().values().get(spreadSheetId, getFeesDetilesfollowupRange).execute()
				.getValues();
	}

	@Override
	public String updateFeesDetiles(String getFeesDetilesfollowupRange, List<Object> list) throws IOException {
		System.err.println("updateFeesDetiles                              " + list);
		log.info("update Fees Detiles is Running");
		ValueRange body = saveOpration.updateDetilesToSheet(list);
		System.err.println(body);
		return sheetsRepository.spreadsheets().values().update(spreadSheetId, getFeesDetilesfollowupRange, body)
				.setValueInputOption("RAW").execute().setSpreadsheetId(spreadSheetId).getUpdatedRange();
	}

	@Override
	public boolean updateDetilesToFollowUp(String followup, List<Object> list) throws IOException {
		log.info("update fees followUp detiles form the sheet   " + list);
		ValueRange value = sheetsRepository.spreadsheets().values().get(spreadSheetId, followup).execute();
		if (value.getValues() != null && value.getValues().size() >= 1) {
			log.info("Fees register sucessfully");
			return saveOpration.saveDetilesWithDataSize(list, followup);

		} else {
			log.info("Fees register sucessfully");
			return saveOpration.saveDetilesWithoutSize(list, followup);
		}
	}

	@Override
	public ValueRange getEmailList(String feesEmailRange) throws IOException {
		ValueRange response = sheetsRepository.spreadsheets().values().get(spreadSheetId, feesEmailRange).execute();
		return response;
	}
}
