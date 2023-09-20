package com.xworkz.dream.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
public interface LoginService {

	public ResponseEntity<String> validateLogin(String email) throws IOException;
	
	public ResponseEntity<String> validateOTP(String email, int otp) throws FileNotFoundException;
	
}
