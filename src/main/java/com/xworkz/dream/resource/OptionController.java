package com.xworkz.dream.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.utils.Dropdown;
import com.xworkz.dream.service.UtilService;

@RestController
@RequestMapping("/utils")
public class OptionController {

	@Autowired
	private UtilService service;
	
	private static final Logger log = LoggerFactory.getLogger(OptionController.class);

	@GetMapping("/dropdown")
	public Dropdown getDropDowns(@RequestHeader String spreadsheetId) {
		  log.info("Request received to fetch dropdown data for spreadsheetId: {}", spreadsheetId);
		Dropdown body = service.getDropdown(spreadsheetId);
		return body;
	}

}
