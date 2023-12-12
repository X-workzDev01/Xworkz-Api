package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.AttendanceDto;

import freemarker.template.TemplateException;

public interface AttendanceService {

	public ResponseEntity<String> writeAttendance(String spreadsheetId, AttendanceDto dto, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException;
	
	public void markAndSaveAbsentDetails(List<AttendanceDto> attendanceDtoList) throws IOException,IllegalAccessException;


}
