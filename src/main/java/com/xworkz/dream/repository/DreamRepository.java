package com.xworkz.dream.repository;

import java.util.List;

public interface DreamRepository {

	List<List<Object>> getDropdown(String spreadsheetId);

	boolean updateLoginInfo(String spreadsheetId, List<Object> row) ;
	
	List<List<Object>> getClientDropDown();

}
