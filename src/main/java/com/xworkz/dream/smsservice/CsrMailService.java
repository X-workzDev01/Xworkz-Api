package com.xworkz.dream.smsservice;

import org.springframework.mail.javamail.MimeMessagePreparator;

public interface CsrMailService {
	public boolean sentCsrMail(MimeMessagePreparator messagePreparator);

	public boolean sentCsrMailDev(MimeMessagePreparator messagePreparator);

}
