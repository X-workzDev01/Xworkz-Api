package com.xworkz.dream.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class UtilDev implements DreamUtil{

	@Value("${mail.smtpHost}")
	private String smtpHost;
	@Value("${mail.smtpPort}")
	private int smtpPort;
	@Value("${mail.userName}")
	private String userName;
	@Value("${mail.password}")
	private String password;

	private static final Logger logger = LoggerFactory.getLogger(UtilLocal.class);

	
	public int generateOTP() {
		// Generate a random OTP
		int otpLength = 6;
		int otpMinValue = 100000;
		int otpMaxValue = 999999;
		Random random = new Random();
		System.out.println("dev Otp");
		return otpMinValue + random.nextInt(otpMaxValue - otpMinValue + 1);
	}

	
	public boolean sendOtptoEmail(String email, int otp) {
		String subject = "OTP for Login";
		String body = "Hi , Your Otp is" + otp + "/n Thank You!";
		logger.debug("Sending email to {}: Subject: {},", email, subject);
		return sendEmail(email, subject, body);
	}

	public boolean sendEmail(String email, String subject, String body) {
		// Email properties
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.office365.com");
		props.put("mail.smtp.port", smtpPort);

		// Create session with authentication
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		});

		try {
			// Create email message
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(userName));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject(subject);
			message.setText(body);

			// Send email
			Transport.send(message);
			logger.info("Email sent to {}: Subject: {}", email, subject);
			return true; // Email sent successfully
		} catch (MessagingException e) {
			logger.error("Failed to send email ", e);
			e.printStackTrace();
			return false; // Failed to send email
		}
	}
	
	public String generateToken() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] tokenBytes = new byte[32];
		secureRandom.nextBytes(tokenBytes);
		return Base64.getEncoder().encodeToString(tokenBytes);
	}
	


}
