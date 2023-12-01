package com.xworkz.dream.scheduler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xworkz.dream.service.BirthadayService;

@Service
public class BirthdaySchedulerImpl {
	@Autowired
	private BirthadayService birthadayService;
	Logger logger = LoggerFactory.getLogger(BirthdaySchedulerImpl.class);

	//@Scheduled(cron = "0 0 0 * * *")
	@Scheduled(fixedRate = 2 * 60 * 1000)
	public void sendBirthdayEmailsScheduled() {
		try {
			logger.info("Running sendBirthday Scheduler");
			birthadayService.sendBirthdayEmails();
		} catch (IOException e) {
			logger.info("Birthday Mail is not working : {} ", e.getMessage());
		}
	}

}
