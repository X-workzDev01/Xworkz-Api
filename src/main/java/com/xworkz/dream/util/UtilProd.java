package com.xworkz.dream.util;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
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

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.Team;
import com.xworkz.dream.service.ChimpMailService;

import freemarker.template.TemplateException;

@Component
@Profile("prod")
public class UtilProd implements DreamUtil {

	@Autowired
	private TemplateEngine templateEngine;
	@Value("${mail.smtpHost}")
	private String smtpHost;
	@Value("${mail.smtpPort}")
	private int smtpPort;
	@Value("${mail.userName}")
	private String userName;
	@Value("${mail.password}")
	private String password;
	@Value("${mailChimp.userName}")
	private String chimpUserName;
	@Autowired
	private ChimpMailService chimpMailService;
	@Value("${mailChimp.templateIdEnquiry}")
	private String templateIdEnquiry;
	@Value("${mailChimp.apiKey}")
	private String apiKey;
	@Value("${mailChimp.SMSusername}")
	private String smsUserName;
	@Value("${mailChimp.route}")
	private String route;
	@Value("${mailChimp.sender}")
	private String sender;
	@Value("${mailChimp.smsType}")
	private String smsType;
	@Value("${mailChimp.smsSuccess}")
	private String smsSuccess;

	@Autowired
	private EncryptionHelper helper;

	private static final Logger logger = LoggerFactory.getLogger(UtilDev.class);

	public int generateOTP() {
		int otpMinValue = 100000;
		int otpMaxValue = 999999;
		Random random = new Random();
		System.out.println("dev Otp");
		return otpMinValue + random.nextInt(otpMaxValue - otpMinValue + 1);
	}

	public boolean sendOtptoEmail(String email, int otp) {
		if (email == null) {
			logger.warn("Email is null");
			return false;
		}
		String subject = "OTP for Login";
		logger.debug("Sending email to {}: Subject: {},", email, subject);
		otpMailService(email, otp, subject);
		return true;

	}

	@Override
	public boolean sendNotificationToEmail(List<Team> teamList, List<FollowUpDto> notificationStatus) {

		if (teamList == null || notificationStatus == null) {
			logger.warn("teamList or notificationStatus is null");
			return false;
		}

		List<String> body = new ArrayList<String>();
		for (int i = 0; i < notificationStatus.size(); i++) {
			body.add(" Candidate name  :" + notificationStatus.get(i).getBasicInfo().getTraineeName() + "\tEmail :"
					+ notificationStatus.get(i).getBasicInfo().getEmail() + "Contactt No :"
					+ notificationStatus.get(i).getBasicInfo().getContactNumber() + "\n");
		}
		String subject = "Follow Up Candidate Details";
		logger.debug("Sending email to {}: Subject: {},", teamList, subject);
		List<String> recipients = new ArrayList<String>();
		teamList.stream().filter(Objects::nonNull).forEach(e -> recipients.add(e.getEmail()));
		sendBulkMailToNotification(recipients, subject, notificationStatus);

		return true;
	}

	public boolean sendEmail(String email, String subject, String body) {
		if (email == null || subject == null || body == null) {
			logger.warn("Email, subject, or body is null");
			return false;
		}

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
		if (email == null || recipientName == null) {
			logger.warn("Email or recipientName is null");
			return false;
		}
		return this.sendCourseContentMailChimp(email, recipientName);
	}

	@Override
	public boolean sendWhatsAppLink(List<String> traineeEmail, String subject, String whatsAppLink) {
		return sendWhatsAppLinkToChimp(traineeEmail, subject, whatsAppLink);

	}

	@Override
	public boolean sendBirthadyEmail(String traineeEmail, String subject, String name) {
		if (traineeEmail == null || name == null) {
			logger.warn("Email or name is null");
			return false;
		}
		return sendBirthadyEmailChimp(traineeEmail, subject, name);
	}

	// ================================================================================================
	// this is mail chimp if use below code send mail through contact@xworkz.in
	private boolean otpMailService(String email, int otp, String subject) {
		Context context = new Context();

		context.setVariable("onetimepass", otp);
		String content = templateEngine.process("otpMailTemplate", context);

		MimeMessagePreparator messagePreparator = mimeMessage -> {

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom(helper.decrypt(chimpUserName));
			messageHelper.setTo(email);
			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
		};

		return chimpMailService.validateAndSendMailByMailOtp(messagePreparator);
	}

	private boolean sendBulkMailToNotification(List<String> recipients, String subject, List<FollowUpDto> body) {
		Context context = new Context();

		context.setVariable("listDto", body);
		String content = templateEngine.process("FollowCandidateFollowupTemplete", context);

		MimeMessagePreparator messagePreparator = mimeMessage -> {

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom(helper.decrypt(chimpUserName));
			for (String recepent : recipients) {
				messageHelper.addTo(new InternetAddress(recepent));
			}
			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
		};

		return chimpMailService.validateAndSendMailByMailId(messagePreparator);
	}

	private boolean sendWhatsAppLinkToChimp(List<String> traineeEmail, String subject, String whatsAppLink) {
		Context context = new Context();

		context.setVariable("whatsAppLink", whatsAppLink);
		String content = templateEngine.process("WhatsAppLinkContentTemplate", context);

		MimeMessagePreparator messagePreparator = mimeMessage -> {

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom(helper.decrypt(chimpUserName));
			for (String recepent : traineeEmail) {

				messageHelper.addTo(new InternetAddress(recepent));
			}
			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
		};

		return chimpMailService.validateAndSendMailByMailId(messagePreparator);
	}

	private boolean sendCourseContentMailChimp(String email, String recipientName)

			throws MessagingException, IOException, TemplateException {

		Context context = new Context();
		context.setVariable("recipientName", recipientName);
		String content = templateEngine.process("CourseContentTemplate", context);
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom(helper.decrypt(chimpUserName));
			messageHelper.setTo(email);
			messageHelper.setSubject("Course Content");
			messageHelper.setText(content, true);
		};

		return chimpMailService.validateAndSendMailByMailId(messagePreparator);
	}

	@Override
	public boolean sms(TraineeDto dto) {

		String response = null;
		String status = null;
		try {
			String mobileNumber = dto.getBasicInfo().getContactNumber().toString();
			if (Objects.nonNull(mobileNumber)) {

				String smsMessage = "Hi " + dto.getBasicInfo().getTraineeName().toString() + "," + "\n"
						+ "Thanks for enquiring with X-workZ for " + "Java Enterpirse Course" + " at " + "Rajajinagara"
						+ "\n" + " For Queries, contact " + "9886971483/9886971480" + "." + "\n"
						+ " Check Mail for Course content (Spam Folder)" + ".";
				;

				logger.debug("smsType is :{} mobileNumber is :{} message is: {}", mobileNumber, smsMessage);

				response = chimpMailService.sendSMS(helper.decrypt(apiKey), helper.decrypt(smsUserName),
						helper.decrypt(sender), mobileNumber, smsMessage, helper.decrypt(smsType),
						helper.decrypt(route), helper.decrypt(templateIdEnquiry));

				logger.info("SingleSMS Result is {}", response);
				JSONObject json = new JSONObject(response);
				status = json.getString("message");
				if (status.equals(helper.decrypt(smsSuccess))) {
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

	private boolean sendBirthadyEmailChimp(String traineeEmail, String subject, String name) {
		Context context = new Context();

		context.setVariable("name", name);
		String content = templateEngine.process("BirthadyaMail", context);

		MimeMessagePreparator messagePreparator = mimeMessage -> {

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom(helper.decrypt(chimpUserName));

			messageHelper.addBcc(new InternetAddress(traineeEmail));

			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
		};

		return chimpMailService.validateAndSendMailByMailId(messagePreparator);
	}

}
