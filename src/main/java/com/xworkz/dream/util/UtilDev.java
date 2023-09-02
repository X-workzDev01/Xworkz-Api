package com.xworkz.dream.util;

import java.io.IOException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.google.api.client.util.ArrayMap;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.utils.Team;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

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

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private Configuration freemarkerConfig;

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

	List<String> body = new ArrayList<String>();

	public boolean sendNotificationToEmail(List<Team> teamList, List<String> candidateName,
			List<String> candidateEmail) {
		for (int i = 0; i < candidateName.size(); i++) {
			body.add(" Candidate name  :" + candidateName.get(i) + "\tEmail :" + candidateEmail.get(i) + "\n");
		}

		String subject = "Follow Up Candidate Detiles";
		logger.debug("Sending email to {}: Subject: {},", teamList, subject);
		teamList.stream().forEach(e -> {
			sendEmail(e.getEmail().toString(), subject, body.toString());

		});

		return true;
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

	@Override
	public boolean sendCourseContent(String email, String recipientName)
			throws MessagingException, IOException, TemplateException {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.office365.com");
			props.put("mail.smtp.port", smtpPort);

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			});
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(userName)); // Replace with your email address
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email)); // Use the provided email
																							// parameter
			message.setSubject("Course Content");

			// Assuming renderFreemarkerTemplate correctly generates the content
			String content = renderJspTemplate("CourseContentTemplate", recipientName);
			message.setContent(content, "text/html; charset=UTF-8");
			Transport.send(message);

			return true; // Email sent successfully
		} catch (MessagingException e) {
			// Handle the messaging exception appropriately
			e.printStackTrace();
			throw e;
		}
	}

	private String renderJspTemplate(String templateName, String recipientName) throws IOException, TemplateException {
		Template template = freemarkerConfig.getTemplate(templateName + ".html"); // Use .ftl extension

		Map<String, Object> model = new HashMap<>();
		model.put("recipientName", recipientName);

		return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
	}

}
