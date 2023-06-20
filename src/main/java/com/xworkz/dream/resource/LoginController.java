package com.xworkz.dream.resource;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.service.LoginService;

@RestController
public class LoginController {

	@Autowired
	private LoginService service;
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam String email) {
		try { 
			logger.info("Logging in with email: {}", email);
			return service.validateLogin(email);
		} catch (IOException e) {

			 logger.error("An error occurred during login", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured");
		}

	}

	@PostMapping("/otp")
	public ResponseEntity<String> validateOtp(@RequestParam String email, @RequestParam int otp) {
		try {
			 logger.info("Validating OTP for email: {}, OTP: {}", email, otp);
			return service.validateOTP(email, otp);
		} catch (FileNotFoundException e) {

			logger.error("An error occurred during OTP validation", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured");
		}
	}
	
	

}
