package com.xworkz.dream.resource;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	public ResponseEntity<String> addEnquiry(@RequestBody EnquiryDto enquiryDto, @RequestHeader String spreadSheetId,
			HttpServletRequest request) {
		log.info("Adding Enquiry Details: {}", enquiryDto);
		boolean saved = service.addEnquiry(enquiryDto, spreadSheetId, request);
		String uri = request.getRequestURI();

		if (saved) {
			log.info("Enquiry Added Successfully");
			return ResponseEntity.ok().body("Enquiry Added Successfully");
		} else {
			log.error("Failed to save the enquiry");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save the enquiry");
		}
	}

}
