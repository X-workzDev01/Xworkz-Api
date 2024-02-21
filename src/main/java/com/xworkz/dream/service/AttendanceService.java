package com.xworkz.dream.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.AbsentDaysDto;
import com.xworkz.dream.dto.AbsenteesDto;
import com.xworkz.dream.dto.AttendanceDataDto;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.AttendanceTrainee;

public interface AttendanceService {

	ResponseEntity<String> writeAttendance(String spreadsheetId, AttendanceDto dto, HttpServletRequest request);

	Boolean traineeAlreadyAdded(String courseName, Integer id);

	List<String> markAndSaveAbsentDetails(List<AbsenteesDto> attendanceDtoList, String batch);

	List<AttendanceTrainee> getTrainee(String batch);

	List<AbsentDaysDto> getAttendanceById(Integer id);

	List<AttendanceDto> getAbsentListByBatch(String batch);

	Boolean markTrainerAttendance(String courseName, Boolean batchAttendanceStatus);

	List<AttendanceDto> addJoined(String courseName);

	ResponseEntity<AttendanceDataDto> attendanceReadData(Integer startingIndex, Integer maxRows, String courseName);

	List<AttendanceDto> filterData(String searchValue, String courseName);

	ResponseEntity<List<AttendanceDto>> getSearchSuggestion(String value, String courseName);
	
}
