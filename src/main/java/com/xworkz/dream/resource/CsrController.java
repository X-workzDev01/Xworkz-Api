package com.xworkz.dream.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.CsrDto;
import com.xworkz.dream.service.CsrService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/csr")
public class CsrController {

	@Autowired
	private CsrService service;

	private static final Logger log = LoggerFactory.getLogger(CsrController.class);

	@ApiOperation(value = "To Register Csr details")
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody CsrDto csrDto, HttpServletRequest request) throws IOException {
		log.info("Registering CSR Details: {}", csrDto);
		boolean saved = service.registerCsr(csrDto, request);

		if (saved) {
			log.info("Registered Successfully");
			return ResponseEntity.ok().body("Registered Successfully");
		} else {
			log.error("Failed to Register");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Register");
		}
	}

	@ApiOperation("To Check Contact number check")
	@GetMapping("/checkcontactNumber")
	public ResponseEntity<String> checkContactNumber(@RequestParam Long contactNumber) throws IOException {
		log.info("Contact number is : {}", contactNumber);
		if (service.checkContactNumber(contactNumber)) {
			return ResponseEntity.ok().body("Contact Number Already Exists");
		} else {
			return ResponseEntity.ok().body("Contact Number Doesn't Exists");
		}
	}

	@ApiOperation("To check USN Number is Exists")
	@GetMapping("/checkUsn")
	public ResponseEntity<String> checkUsnNumber(@RequestParam String usnNumber) throws IOException {
		log.info("checking USN number, {}", usnNumber);
		if (service.checkUsnNumber(usnNumber)) {
			return ResponseEntity.ok().body("Usn Number Already Exists");
		} else {
			return ResponseEntity.ok().body("Usn Number Doesn't Exists");
		}
	}

	@ApiOperation("To check Unique Number is Exists")
	@GetMapping("/checkUniqueNumber")
	public ResponseEntity<String> checkUniqueNumber(@RequestParam String uniqueNumber) throws IOException {
		log.info("checking unique number {}", uniqueNumber);
		if (service.checkUniqueNumber(uniqueNumber)) {
			return ResponseEntity.ok().body("Unique Number Already Exists");
		} else {
			return ResponseEntity.ok().body("Unique Number Doesn't Exists");
		}

	}

}