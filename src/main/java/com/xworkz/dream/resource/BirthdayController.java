package com.xworkz.dream.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.BirthadayService;

import io.swagger.annotations.ApiOperation;
@RestController
@RequestMapping("/api")
public class BirthdayController {
	@Autowired
	private BirthadayService service;
	
	
	@ApiOperation(value = "To update Birth day info while registering")
	@PostMapping("/birthDayInfo")
	public ResponseEntity<String> updateBirthDayInfo(@RequestHeader String spreadsheetId, @RequestBody TraineeDto dto,
			HttpServletRequest request) throws IllegalAccessException, IOException {
		return service.saveBirthDayInfo(spreadsheetId, dto, request);

	}

}