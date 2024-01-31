package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.xworkz.dream.dto.AbsentDaysDto;
import com.xworkz.dream.dto.AbsenteesDto;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.AttendanceTrainee;

import freemarker.template.TemplateException;

public interface AttendanceService {

	public ResponseEntity<String> writeAttendance(String spreadsheetId, AttendanceDto dto, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException, IllegalAccessException;

	public Boolean traineeAlreadyAdded(String courseName, Integer id) throws IOException;

	public void markAndSaveAbsentDetails(List<AbsenteesDto> attendanceDtoList, String batch)
			throws IOException, IllegalAccessException;

	public List<AttendanceTrainee> getTrainee(String batch);

	public List<AbsentDaysDto> getAttendanceById(Integer id);

	public List<AttendanceDto> getAbsentListByBatch(String batch) throws IOException;

	public Boolean markTraineeAttendance(String courseName, Boolean batchAttendanceStatus)
			throws IOException, IllegalAccessException;

}
