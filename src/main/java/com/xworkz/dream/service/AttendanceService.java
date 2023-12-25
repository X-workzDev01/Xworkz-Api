package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.xworkz.dream.dto.AbsenteesDto;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.AttendanceTrainee;

import freemarker.template.TemplateException;

public interface AttendanceService {

	public ResponseEntity<String> writeAttendance(String spreadsheetId, AttendanceDto dto, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException;
	
	public void markAndSaveAbsentDetails(@RequestBody List<AbsenteesDto> attendanceDtoList , @RequestParam String batch) throws IOException,IllegalAccessException;

	public List<AttendanceTrainee> getTrainee(String batch);

}
