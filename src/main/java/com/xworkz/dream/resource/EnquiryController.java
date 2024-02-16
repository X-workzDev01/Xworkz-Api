package com.xworkz.dream.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.service.EnquiryService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class EnquiryController {

	@Autowired
	private EnquiryService service;

	private static final Logger log = LoggerFactory.getLogger(EnquiryController.class);

	@ApiOperation(value = "To Add Enquiry Details")
	@PostMapping("/enquiry")
	public ResponseEntity<String> addEnquiry(@RequestBody EnquiryDto enquiryDto, @RequestHeader String spreadSheetId) {
		log.info("Adding Enquiry Details: {}", enquiryDto);
		return ResponseEntity.ok(service.addEnquiry(enquiryDto, spreadSheetId));
	}

}
