package com.xworkz.dream.service;

import org.springframework.mail.javamail.MimeMessagePreparator;

 public interface ChimpMailService {
	 boolean validateAndSendMailByMailId(MimeMessagePreparator messagePreparator);

	 boolean validateAndSendBirthdayMail(MimeMessagePreparator messagePreparator);

	 void  validateAndSendMail(MimeMessagePreparator messagePreparator);

	 boolean validateAndSendMailByMailOtp(MimeMessagePreparator messagePreparator);

	 String sendSMS(String apiKey, String username, String sender, String phone, String message, String smsType,
			String route, String templateId);

	 boolean validateAndSendMailByMailIdDev(MimeMessagePreparator messagePreparator);

}
