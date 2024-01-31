package com.xworkz.dream.service;

import java.io.IOException;

public interface DreamService {

	public String verifyEmails(String email);

	public Boolean addJoined(String status,String courseName) throws IOException,IllegalAccessException;
	
}
