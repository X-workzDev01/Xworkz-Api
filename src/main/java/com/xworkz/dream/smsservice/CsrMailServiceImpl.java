package com.xworkz.dream.smsservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.xworkz.dream.configuration.MailConfig;
import com.xworkz.dream.service.ChimpmailServiceImpl;

@Service
public class CsrMailServiceImpl implements CsrMailService {
	@Autowired
	private MailConfig config;

	private static final Logger log = LoggerFactory.getLogger(ChimpmailServiceImpl.class);

	@Override
	@Async
	public boolean sentCsrMailDev(MimeMessagePreparator messagePreparator) {
		log.info("invoked validateAndSendMailByMailId of SpringMailServiceImpl...");

		try {
			config.getMailSenderDev().send(messagePreparator);
			log.info("Mail sent successfully");
			return true;
		} catch (MailException e) {
			log.info("Mail sent Faild!");
			log.error(e.getMessage(), e);
			return false;
		}

	}
	@Override
	@Async
	public boolean sentCsrMail(MimeMessagePreparator messagePreparator) {
		log.info("invoked validateAndSendMailByMailId of SpringMailServiceImpl...");

		try {
			config.getMailSender().send(messagePreparator);
			log.info("Mail sent successfully");
			return true;
		} catch (MailException e) {
			log.info("Mail sent Faild!");
			log.error(e.getMessage(), e);
			return false;
		}

	}

}
