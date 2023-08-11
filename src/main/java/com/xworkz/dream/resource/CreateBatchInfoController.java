package com.xworkz.dream.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.DreamService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class CreateBatchInfoController {
	
	
	Logger logger = LoggerFactory.getLogger(CreateBatchInfoController.class);

	@Autowired
	private DreamService service;
	
	@ApiOperation(value = "To register the upcoming batch details")
	@PostMapping("/batchInfo")
	public ResponseEntity<String> batchDetails(@RequestHeader String spreadsheetId, @RequestBody BatchDetailsDto dto,
			HttpServletRequest request) throws IOException, IllegalAccessException {
		logger.info("Registering trainee details: {}",dto);
		return service.saveDetails(spreadsheetId, dto, request);
	}
	

	
}
