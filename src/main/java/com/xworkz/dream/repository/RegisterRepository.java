package com.xworkz.dream.repository;

import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface RegisterRepository {


	boolean writeData(String spreadsheetId, List<Object> row) ;

	List<List<Object>> getEmails(String spreadsheetId, String email);

	List<List<Object>> getContactNumbers(String spreadsheetId);

	List<List<Object>> readData(String spreadsheetId);

	UpdateValuesResponse update(String spreadsheetId, String range2, ValueRange valueRange);

	List<List<Object>> getEmailsAndNames(String spreadsheetId, String value);

	List<List<Object>> getAlternativeNumber(String spreadsheetId);

	List<List<Object>> getUsnNumber(String spreadsheetId);

	List<List<Object>> getUniqueNumbers(String spreadsheetId);
}
