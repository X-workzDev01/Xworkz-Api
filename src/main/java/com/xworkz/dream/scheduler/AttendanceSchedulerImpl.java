package com.xworkz.dream.scheduler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xworkz.dream.repository.AttendanceRepository;

@Service
public class AttendanceSchedulerImpl {
	@Autowired
	private AttendanceRepository repository;
	@Value("${login.sheetId}")
	private String spreadsheetId;

	Logger log = LoggerFactory.getLogger(AttendanceSchedulerImpl.class);

	@Scheduled(fixedRate = 60 * 24 * 60 * 1000) // 1000 is equal to 1 second
	public void schudulerAttandance() throws IOException {
		log.info("Cleared Day attendance");
		repository.clearColumnData(spreadsheetId, "attendanceInfo!J2:J");
		repository.clearColumnData(spreadsheetId, "attendanceInfo!K2:k");
		repository.clearColumnData(spreadsheetId, "attendanceInfo!L2:L");

	}

}
