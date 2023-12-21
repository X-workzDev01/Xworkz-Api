package com.xworkz.dream.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface ClientRepository {

	void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException;

	 boolean writeClientInformation(List<Object> row) throws IOException;
	 List<List<Object>> readData() throws IOException;

	UpdateValuesResponse updateclientInfor(String range, ValueRange valueRange) throws IOException;

}
