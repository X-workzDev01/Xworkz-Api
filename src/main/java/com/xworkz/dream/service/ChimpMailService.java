package com.xworkz.dream.service;

import org.springframework.mail.javamail.MimeMessagePreparator;


public interface ChimpMailService {
	public boolean validateAndSendMailByMailId(MimeMessagePreparator messagePreparator);
	
	public void validateAndSendBirthdayMail(MimeMessagePreparator messagePreparator);
	
	public void validateAndSendMail(MimeMessagePreparator messagePreparator);

	public boolean validateAndSendMailByMailOtp(MimeMessagePreparator messagePreparator);

	public boolean sms();

	public String sendSMS(String apiKey, String username, String sender, String phone, String message, String smsType,
			String route, String templateId);
	public boolean validateAndSendMailByMailIdDev(MimeMessagePreparator messagePreparator) ;

}
