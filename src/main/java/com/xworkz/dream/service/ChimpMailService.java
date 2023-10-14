package com.xworkz.dream.service;

import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;

public interface ChimpMailService {
	public boolean validateAndSendMailByMailId(MimeMessagePreparator messagePreparator);

	public boolean validateAndSendMailByMailOtp(MimeMessagePreparator messagePreparator);

	public boolean sms();

	public String sendSMS(String apiKey, String username, String sender, String phone, String message, String smsType,
			String route, String templateId);
}
