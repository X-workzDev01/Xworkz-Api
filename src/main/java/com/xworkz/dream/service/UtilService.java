package com.xworkz.dream.service;

import com.xworkz.dream.dto.utils.ClientDropDown;
import com.xworkz.dream.dto.utils.Dropdown;
public interface UtilService {

	 Dropdown getDropdown(String spreadsheetId);

	ClientDropDown getClientDropDown();
}
