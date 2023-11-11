package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

public interface BirthadayRepository {
	
	public ValueRange getBirthDayId(String spreadsheetId) throws IOException;

	public boolean saveBirthDayDetails(String spreadsheetId, List<Object> row) throws IOException;

	public List<List<Object>> getBirthadayDetails(String spreadsheetId) throws IOException;

}
