package com.xworkz.dream.repository;

import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface ClientHrRepository {

	boolean saveClientHrInformation(List<Object> row);

	List<List<Object>> readData();

	UpdateValuesResponse updateHrDetails(String range, ValueRange valueRange);
}
