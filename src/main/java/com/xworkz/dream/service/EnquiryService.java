package com.xworkz.dream.service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.dto.TraineeDto;

import freemarker.template.TemplateException;

public interface EnquiryService {
	
	public ResponseEntity<String> writeDataEnquiry(String spreadsheetId, TraineeDto dto, HttpServletRequest request)throws MessagingException , TemplateException;
	
	
	public boolean addEnquiry(EnquiryDto enquiryDto, String spreadsheetId, HttpServletRequest request);
}
