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

import com.xworkz.dream.dto.CsrDto;
import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.service.CsrService;
import com.xworkz.dream.service.EnquiryService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/csr")
public class CsrController {

	@Autowired
	private CsrService service;

	private static final Logger log = LoggerFactory.getLogger(CsrController.class);

	@ApiOperation(value = "To Register Csr details")
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody CsrDto csrDto,
			HttpServletRequest request) {
		log.info("Registering CSR Details: {}", csrDto);
		boolean saved = service.registerCsr(csrDto,request);
		

		if (saved) {
			log.info("CSR Resgitered Sucessfully");
			return ResponseEntity.ok().body("CSR Resgitered Sucessfully");
		} else {
			log.error("Failed to Register Csr");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Register Csr");
		}
	}

}