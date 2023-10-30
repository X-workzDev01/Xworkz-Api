package com.xworkz.dream.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.xworkz.dream.configuration.MailConfig;

@Service
public class ChimpmailServiceImpl implements ChimpMailService {


	@Autowired
	private MailConfig config;

	private Logger logger = LoggerFactory.getLogger(ChimpmailServiceImpl.class);

//	@Async
	@Override
	public boolean validateAndSendMailByMailId(MimeMessagePreparator messagePreparator) {
		logger.info("invoked validateAndSendMailByMailId of SpringMailServiceImpl...");

		try {
			config.getMailSender().send(messagePreparator);
			logger.info("Mail sent successfully");
			return true;
		} catch (MailException e) {
			logger.info("Mail sent Faild!");
			logger.error(e.getMessage(), e);
			return false;
		}

	}

	@Override
	public boolean validateAndSendMailByMailOtp(MimeMessagePreparator messagePreparator) {
		logger.info("invoked validateAndSendMailByMailId of SpringMailServiceImpl...");

		try {
			config.getMailSender().send(messagePreparator);
			logger.info("Mail sent successfully");
			return true;
		} catch (MailException e) {
			logger.info("Mail sent Faild!");
			logger.error(e.getMessage(), e);
			return false;
		}

	}

	public boolean sms() {
		String response = null;
		String status = null;
		try {
			String mobileNumber = "9900775088";

			if (Objects.nonNull(mobileNumber)) {

				String smsMessage = "Hi " + "Akshara" + "," + "\n" + "Thanks for enquiring with X-workZ for "
						+ "Java Enterpirse Course" + " at " + "Rajajinagara" + "\n" + " For Queries, contact "
						+ "9886971483/9886971480" + "." + "\n" + " Check Mail for Course content (Spam Folder)" + ".";
				;

				logger.debug("smsType is :{} mobileNumber is :{} message is: {}", mobileNumber, smsMessage);

				response = this.sendSMS("25C45-0AE7A", "xworkzodc", "XWORKZ", mobileNumber, smsMessage, "Text", "Scrub",
						"1607100000000285908");

				logger.info("SingleSMS Result is {}", response);
				JSONObject json = new JSONObject(response);
				status = json.getString("message");
				if (status.equals("SMS Sent Successfully")) {
					return true;
				} else {
					logger.info("SingleSMS Result is {}", response);
					return false;
				}

			} else {
				logger.info("SingleSMS Result is {}", response);
				return false;
			}

		} catch (Exception e) {
			logger.error("\n\nMessage is {} and exception is {}\n\n\n\n\n", e.getMessage(), e);
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
			logger.info(requestUrl);

			URL url = new URL(requestUrl);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			uc.setDoOutput(true);
			uc.setRequestMethod("POST");

			DataOutputStream wr = new DataOutputStream(uc.getOutputStream());
			wr.write(requestUrl.toString().getBytes());
			BufferedReader bufferedReader = null;
			if (uc.getResponseCode() == 200) {
				logger.debug("Response code is {}", uc.getResponseCode());
				bufferedReader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
				logger.debug("Success URL is {}", uc);
				logger.info("SMS Sent Succesfuly to +" + phone);
				while ((line = bufferedReader.readLine()) != null) {
					content.append(line).append("\n");
				}
				bufferedReader.close();
				logger.debug("Content is {}", content);
				return content.toString();
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(uc.getErrorStream()));
				logger.debug("Fail contact number is {}", requestUrl.length());
				logger.info("SMS Sent Faild to +" + phone);
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return null;
	}
}