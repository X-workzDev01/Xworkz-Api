package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface BirthadayRepository {

	boolean saveBirthDayDetails(String spreadsheetId, List<Object> row) throws IOException;

    List<List<Object>> getBirthadayDetails(String spreadsheetId) throws IOException;

	UpdateValuesResponse updateDob(String rowRange, ValueRange valueRange) throws IOException;

}
