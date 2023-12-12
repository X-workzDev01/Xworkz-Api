package com.xworkz.dream.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xworkz.dream.repository.AttendanceRepository;
@Service
public class AttendanceSchedulerImpl {

	@Autowired
	private AttendanceRepository repository;
	@Value("${login.sheetId}")
	private String spreadsheetId;

	Logger log = LoggerFactory.getLogger(AttendanceSchedulerImpl.class);

}
