package com.xworkz.dream.service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.dto.TraineeDto;

import freemarker.template.TemplateException;

public interface EnquiryService {
	 ResponseEntity<String> writeDataEnquiry(String spreadsheetId, TraineeDto dto, HttpServletRequest request)throws MessagingException , TemplateException;
	
	 boolean addEnquiry(EnquiryDto enquiryDto, String spreadsheetId, HttpServletRequest request);
}
