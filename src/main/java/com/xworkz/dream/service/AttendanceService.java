package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.xworkz.dream.dto.AttadanceSheetDto;
import com.xworkz.dream.dto.AttendanceDto;

import freemarker.template.TemplateException;

public interface AttendanceService {

	public ResponseEntity<String> writeAttendance(String spreadsheetId, AttendanceDto dto, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException;

	public ResponseEntity<String> everyDayAttendance(AttendanceDto dto, HttpServletRequest request) throws Exception;

	ResponseEntity<AttadanceSheetDto> getAttendanceDetilesByEmail(String Email, int startIndex, int maxRows)
			throws IOException, MessagingException, TemplateException;

	public ResponseEntity<AttadanceSheetDto> getAttendanceDetilesBatchAndDate(String batch, String date, int startIndex,
			int maxRows) throws IOException, MessagingException, TemplateException;

	public ResponseEntity<AttadanceSheetDto> getAttendanceDetilesBatch(String batch, int startIndex, int maxRows)
			throws IOException, MessagingException, TemplateException;

}
