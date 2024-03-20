package com.xworkz.dream.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.xworkz.dream.configuration.MailConfig;

@Service
public class ChimpmailServiceImpl implements ChimpMailService {
	@Autowired
	private MailConfig config;
	private static final Logger log = LoggerFactory.getLogger(ChimpmailServiceImpl.class);

	@Override
	public boolean validateAndSendMailByMailIdDev(MimeMessagePreparator messagePreparator) {
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
	public void  validateAndSendMail(MimeMessagePreparator messagePreparator) {
		log.info("invoked validateAndSendMailByMailId of SpringMailServiceImpl...");

		try {
			config.getMailSenderDev().send(messagePreparator);
			log.info("Mail sent successfully");
		} catch (MailException e) {
			log.info("Mail sent Faild!");
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public boolean validateAndSendMailByMailId(MimeMessagePreparator messagePreparator) {
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

	@Override
	public boolean validateAndSendBirthdayMail(MimeMessagePreparator messagePreparator) {
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

	@Override
	public boolean validateAndSendMailByMailOtp(MimeMessagePreparator messagePreparator) {
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

	@Override
	public String sendSMS(String apiKey, String username, String sender, String phone, String message, String smsType,
			String route, String templateId) {
		StringBuilder content = new StringBuilder();
		String line;
		try {
			String requestUrl = "http://www.k3digitalmedia.co.in/websms/api/http/index.php?" + "&username="
					+ URLEncoder.encode(username, "UTF-8") + "&apikey=" + URLEncoder.encode(apiKey, "UTF-8")
					+ "&apirequest=" + URLEncoder.encode(smsType, "UTF-8") + "&route="
					+ URLEncoder.encode(route, "UTF-8") + "&sender=" + URLEncoder.encode(sender, "UTF-8") + "&mobile="
					+ URLEncoder.encode(phone, "UTF-8") + "&message=" + URLEncoder.encode(message, "UTF-8")
					+ "&TemplateID=" + URLEncoder.encode(templateId, "UTF-8");
			log.info(requestUrl);

			URL url = new URL(requestUrl);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			uc.setDoOutput(true);
			uc.setRequestMethod("POST");

			DataOutputStream wr = new DataOutputStream(uc.getOutputStream());
			wr.write(requestUrl.toString().getBytes());
			BufferedReader bufferedReader = null;
			if (uc.getResponseCode() == 200) {
				log.debug("Response code is {}", uc.getResponseCode());
				bufferedReader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
				log.debug("Success URL is {}", uc);
				log.info("SMS Sent Succesfuly to +" + phone);
				while ((line = bufferedReader.readLine()) != null) {
					content.append(line).append("\n");
				}
				bufferedReader.close();
				log.debug("Content is {}", content);
				return content.toString();
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(uc.getErrorStream()));
				log.debug("Fail contact number is {}", requestUrl.length());
				log.info("SMS Sent Faild to +" + phone);
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return null;
	}
}