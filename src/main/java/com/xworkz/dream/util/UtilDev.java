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
public class UtilDev implements DreamUtil {

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
		String body = "Hi , Your Otp is  " + otp + "   Thank You!";
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
			System.out.println("");
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

	public boolean sendCourseContent(String email,String name) {
		String subject = "Java Full Stack Course Content";
		String body = "Dear Candidate,\n\n"
				+ "Please find the attached Java Full Stack Course Content for your reference.\n\n"
				+ "The attached file contains the overview of the course. Apart from the mentioned course, "
				+ "you will be learning a total of 15 Technologies and Placement activities, technical aptitude, "
				+ "Resume Building, Mocks, Presentation, etc.\n\n"
				+ "For any queries, feel free to reach me at 9886971483/9886971480.\n\n"
				+ "Follow us on Facebook and Instagram for more updates regarding new batches and placement details:\n"
				+ "Facebook: https://www.facebook.com/xworkzdevelopmentcenter/\n"
				+ "Instagram: https://www.instagram.com/xworkzodc1?r=nametag\n"
				+ "Google Reviews Link: https://g.page/xworkzodc/review?av\n\n" + "Thanks and Regards,\n"
				+ "HR Team\n" + "M.No: 9845958884\n\n"
				+ "Find us on Google Maps: https://goo.gl/maps/LSgLsZ6oqRu\n\n" + "https://www.x-workz.in\n"
				+ "080-48669257/9845958884\n" + "RAJAJINAGAR||BTM";
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
			System.out.println("");
			logger.error("Failed to send email ", e);
			e.printStackTrace();
			return false; // Failed to send email
		}
	}
}
