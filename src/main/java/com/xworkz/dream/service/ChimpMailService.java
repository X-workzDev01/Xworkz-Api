package com.xworkz.dream.service;

import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;

public interface ChimpMailService {
	public boolean validateAndSendMailByMailId(MimeMessagePreparator messagePreparator);

	@Async
	public void mailService();

	public boolean sendOTPSMS();

}
