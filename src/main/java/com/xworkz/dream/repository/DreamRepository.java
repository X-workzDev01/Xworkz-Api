package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

public interface DreamRepository {

	List<List<Object>> getDropdown(String spreadsheetId) throws IOException;

	boolean updateLoginInfo(String spreadsheetId, List<Object> row) throws IOException;
	
	List<List<Object>> getClientDropDown();

}
