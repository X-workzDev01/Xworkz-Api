package com.xworkz.dream.repository;

import java.io.IOException;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface WhatsAppRepository {
	
	public UpdateValuesResponse updateWhatsAppLink(String spreadsheetId, String range2, ValueRange valueRange) throws IOException;

}
