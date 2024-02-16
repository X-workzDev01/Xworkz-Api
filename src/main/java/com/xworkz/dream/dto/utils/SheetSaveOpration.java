package com.xworkz.dream.dto.utils;

import java.util.List;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

public interface SheetSaveOpration {
	boolean saveDetilesWithoutSize(List<Object> list, String feesRegisterRange);

	boolean saveDetilesWithDataSize(List<Object> list, String feesRegisterRange);

	Sheets ConnsetSheetService();

	ValueRange updateDetilesToSheet(List<Object> list);

}
