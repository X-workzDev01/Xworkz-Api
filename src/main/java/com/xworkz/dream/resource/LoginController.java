package com.xworkz.dream.resource;

import java.io.FileNotFoundException;
import java.io.IOException;

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

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam String email) {
		try {
			return service.validateLogin(email);
		} catch (IOException e) {

			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured");
		}

	}

	@PostMapping("/otp")
	public ResponseEntity<String> validateOtp(@RequestParam String email, @RequestParam int otp) {
		try {
			return service.validateOTP(email, otp);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured");
		}
	}

}
