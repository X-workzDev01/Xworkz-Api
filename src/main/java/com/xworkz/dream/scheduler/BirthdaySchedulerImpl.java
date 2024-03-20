package com.xworkz.dream.scheduler;

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

	@Scheduled(cron = "0 0 0 * * *")
	public void sendBirthdayEmailsScheduled() {
		logger.info("Running sendBirthday Scheduler");
		birthadayService.sendBirthdayEmails();
	}

	 @Scheduled(fixedRate = 31536000000L)
	public void updateEmailSentStatus() {
		logger.info("update email status in birthday sheet ");
		birthadayService.updateBirthDayMailStatusYearly();
	}

}
