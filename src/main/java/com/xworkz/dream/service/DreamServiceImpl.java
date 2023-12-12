package com.xworkz.dream.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xworkz.dream.interfaces.EmailableClient;

@Service
public class DreamServiceImpl implements DreamService {

	@Autowired
	private EmailableClient emailableClient;
	@Value("${sheets.liveKey}")
	private String API_KEY;

	private static final Logger logger = LoggerFactory.getLogger(DreamServiceImpl.class);


	@Override
	public String verifyEmails(String email) {
		 logger.info("Verifying email: {}", email);
		return emailableClient.verifyEmail(email, API_KEY);
	}

}
