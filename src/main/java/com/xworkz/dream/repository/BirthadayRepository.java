package com.xworkz.dream.repository;

import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

public interface BirthadayRepository {

	boolean saveBirthDayDetails( List<Object> row);

    List<List<Object>> getBirthadayDetails(String spreadsheetId);

    String updateDob(String rowRange, ValueRange valueRange);
	List<List<Object>> getBirthadayEmailList();

}
