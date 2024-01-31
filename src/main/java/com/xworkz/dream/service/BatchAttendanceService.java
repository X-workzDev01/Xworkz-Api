package com.xworkz.dream.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.BatchAttendanceDto;

public interface BatchAttendanceService {

	ResponseEntity<String> writeBatchAttendance(BatchAttendanceDto batchAttendanceDto)
			throws IOException,IllegalAccessException;
	
	Boolean getPresentDate(String courseName) throws IOException;

}