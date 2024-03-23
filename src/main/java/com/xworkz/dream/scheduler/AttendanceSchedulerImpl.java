package com.xworkz.dream.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.service.AttendanceService;
@Service
public class AttendanceSchedulerImpl {
	@Autowired
	private AttendanceService service;

	Logger log = LoggerFactory.getLogger(AttendanceSchedulerImpl.class);
	
//	 @Scheduled(cron = "0 0 16 * * *")
	public void attendanceFollowUp() {
		service.processAttendanceData();
	}

}
