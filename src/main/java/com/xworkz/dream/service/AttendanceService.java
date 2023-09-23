package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.xworkz.dream.dto.AttendanceDto;

import freemarker.template.TemplateException;

public interface AttendanceService {

	public ResponseEntity<String> writeAttendance(@RequestHeader String spreadsheetId, @RequestBody AttendanceDto dto,
			HttpServletRequest request) throws IOException, MessagingException, TemplateException;

	public ResponseEntity<String> everyDayAttendance(AttendanceDto dto, HttpServletRequest request) throws Exception;

	ResponseEntity<List<AttendanceDto>> getAttendanceDetilesByEmail(String Email)
			throws IOException, MessagingException, TemplateException;

	public ResponseEntity<List<AttendanceDto>> getAttendanceDetilesBatchAndDate(String batch, String date)
			throws IOException, MessagingException, TemplateException;

	public ResponseEntity<List<AttendanceDto>> getAttendanceDetilesBatch(String batch)
			throws IOException, MessagingException, TemplateException;

}
