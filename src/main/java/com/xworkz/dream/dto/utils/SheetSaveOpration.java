package com.xworkz.dream.dto.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface SheetSaveOpration {
	public boolean saveDetilesWithoutSize(List<Object> list, String feesRegisterRange);

	public boolean saveDetilesWithDataSize(List<Object> list, String feesRegisterRange);

	public Sheets ConnsetSheetService();

	public ValueRange updateDetilesToSheet(List<Object> list);

}
