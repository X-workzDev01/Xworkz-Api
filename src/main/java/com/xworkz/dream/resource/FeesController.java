package com.xworkz.dream.resource;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.FeesDto;
import com.xworkz.dream.service.FeesService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RequestMapping("/api")
@RestController
public class FeesController {
	@Autowired
	private FeesService feesService;
	private Logger log = LoggerFactory.getLogger(FeesController.class);

	@ApiOperation("Saving feesDetiles  ")
	@PostMapping("/saveFees")
	public ResponseEntity<String> writeFeesSaveOpration(FeesDto dto) throws IOException {
		log.info("Running save Fees detiles controller ");
		String serviceResponse = feesService.writeFeesDetiles(dto);

		return ResponseEntity.ok(serviceResponse);

	}

}
