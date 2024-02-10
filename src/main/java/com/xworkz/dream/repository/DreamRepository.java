package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

public interface DreamRepository {

	ValueRange getIds(String spreadsheetId) throws IOException;

	List<List<Object>> getDropdown(String spreadsheetId) throws IOException;

	boolean updateLoginInfo(String spreadsheetId, List<Object> row) throws IOException;

}
