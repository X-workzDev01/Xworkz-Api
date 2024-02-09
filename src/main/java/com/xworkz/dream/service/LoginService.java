package com.xworkz.dream.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
public interface LoginService {

	 ResponseEntity<String> validateLogin(String email) throws IOException;
	
	 ResponseEntity<String> validateOTP(String email, int otp) throws FileNotFoundException;
	
}
