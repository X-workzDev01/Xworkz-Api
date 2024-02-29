package com.xworkz.dream.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.utils.ClientDropDown;
import com.xworkz.dream.dto.utils.Dropdown;
import com.xworkz.dream.service.UtilService;

@RestController
@RequestMapping("/utils")
public class OptionController {

	@Autowired
	private UtilService service;


	@GetMapping("/dropdown")
	public Dropdown getDropDowns(@RequestHeader String spreadsheetId) {
		Dropdown body = service.getDropdown(spreadsheetId);
		return body;
	}

	@GetMapping("/clientdropdown")
	public ClientDropDown getClientDropDown() {
		ClientDropDown clientDropDown = service.getClientDropDown();
		return clientDropDown;
	}

}
