package com.xworkz.dream.util;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
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
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.service.ChimpMailService;
import com.xworkz.dream.smsservice.CSRSMSService;
import com.xworkz.dream.smsservice.CsrMailService;
import com.xworkz.dream.userYml.TeamList;

import freemarker.template.TemplateException;

@Component
@Profile("dev")
public class UtilDev implements DreamUtil {

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
	@Autowired
	private EncryptionHelper helper;
	@Autowired
	private CsrMailService csrMailService;
	@Autowired
	private CSRSMSService csrSmsService;
	@Autowired
	private TeamList team;

	private static final Logger logger = LoggerFactory.getLogger(UtilDev.class);

	public int generateOTP() {
		int otpMinValue = 100000;
		int otpMaxValue = 999999;
		Random random = new Random();
		logger.info("dev Otp");
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
					+ notificationStatus.get(i).getBasicInfo().getEmail() + "Contact No :"
					+ notificationStatus.get(i).getBasicInfo().getContactNumber() + "\n");
		}
		String subject = "Follow Up Candidate Details";
		logger.debug("Sending email to {}: Subject: {},", teamList, subject);
		List<String> recipients = new ArrayList<String>();
		teamList.stream().filter(Objects::nonNull).forEach(e -> recipients.add(e.getEmail()));
		sendBulkMailToNotification(recipients, subject, notificationStatus);

		return true;
	}

	@Override
	public boolean sendFeesNotificationToEmail(List<Team> teamList, List<FeesDto> notificationStatus) {
		if (teamList == null || notificationStatus == null) {
			logger.warn("teamList or notificationStatus is null");
			return false;
		}

		List<String> body = new ArrayList<String>();
		for (int i = 0; i < notificationStatus.size(); i++) {
			body.add(" Candidate name  :" + notificationStatus.get(i).getName() + "\tEmail :"
					+ notificationStatus.get(i).getFeesHistoryDto().getEmail() + "\n");
		}
		String subject = "Fees Follow Up Candidate Details";
		logger.debug("Sending email to {}: Subject: {},", teamList, subject);
		List<String> recipients = new ArrayList<String>();
		teamList.stream().filter(Objects::nonNull).forEach(e -> recipients.add(e.getEmail()));
		sendBulkMailToFeesNotification(recipients, subject, notificationStatus);

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

		} else {
			sendBirthadyEmailChimp(traineeEmail, subject, name);
			return true;
		}
	}

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
			messageHelper.setFrom("hareeshahr.xworkz@gmail.com");
			messageHelper.addTo(recipients.get(0));
			for (String recepent : recipients) {
				messageHelper.addCc(new InternetAddress(recepent));
			}
			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
		};

		return chimpMailService.validateAndSendMailByMailIdDev(messagePreparator);
	}

	private boolean sendBulkMailToFeesNotification(List<String> recipients, String subject, List<FeesDto> body) {
		Context context = new Context();

		context.setVariable("listDto", body);
		String content = templateEngine.process("FeesFollowupTemplete", context);

		MimeMessagePreparator messagePreparator = mimeMessage -> {

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom("hareeshahr.xworkz@gmail.com");
//			messageHelper.addTo(recipients.get(0));
			messageHelper.addTo("suhasnb.xworkz@gmail.com");
			for (String recepent : recipients) {
//				messageHelper.addCc(new InternetAddress(recepent));
			}
			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
		};

		return chimpMailService.validateAndSendMailByMailIdDev(messagePreparator);
	}

	private boolean sendWhatsAppLinkToChimp(List<String> traineeEmail, String subject, String whatsAppLink) {
		Context context = new Context();

		context.setVariable("whatsAppLink", whatsAppLink);
		String content = templateEngine.process("WhatsAppLinkContentTemplate", context);

		MimeMessagePreparator messagePreparator = mimeMessage -> {

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom("hareeshahr.xworkz@gmail.com");
			for (String recepent : traineeEmail) {
				messageHelper.addTo(new InternetAddress(recepent));
			}
			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
		};

		return chimpMailService.validateAndSendMailByMailIdDev(messagePreparator);
	}

	private boolean sendCourseContentMailChimp(String email, String recipientName)

			throws MessagingException, IOException, TemplateException {

		Context context = new Context();
		context.setVariable("recipientName", recipientName);
		String content = templateEngine.process("CourseContentTemplate", context);
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom("hareeshahr.xworkz@gmail.com");
			messageHelper.setTo(email);
			messageHelper.setSubject("Course Content");
			messageHelper.setText(content, true);
		};

		return chimpMailService.validateAndSendMailByMailIdDev(messagePreparator);
	}

	@Override
	public boolean sms(TraineeDto dto) {
		return true;
	}

	private boolean sendBirthadyEmailChimp(String traineeEmail, String subject, String name) {
		Context context = new Context();

		context.setVariable("name", name);
		String content = templateEngine.process("BirthadyaMail", context);

		MimeMessagePreparator messagePreparator = mimeMessage -> {

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom("hareeshahr.xworkz@gmail.com");
			messageHelper.addCc("hr@x-workz.in");
			messageHelper.setTo(traineeEmail);

			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
		};
		chimpMailService.validateAndSendMail(messagePreparator);
		return true;
	}

	@Override
	public boolean csrEmailSent(TraineeDto dto) {
		Context context = new Context();
		if (dto.getCourseInfo().getOfferedAs().equalsIgnoreCase("CSR")) {
			context.setVariable("name", dto.getBasicInfo().getTraineeName());
			context.setVariable("usnNumber", dto.getCsrDto().getUsnNumber());
			context.setVariable("collegeName", dto.getEducationInfo().getCollegeName());
			context.setVariable("uniqueID", dto.getCsrDto().getUniqueId());
			String content = templateEngine.process("CSRMailTemplate", context);

			MimeMessagePreparator messagePreparator = mailSentCSRDrive(dto, content);
			return csrMailService.sentCsrMail(messagePreparator);

		} else {
			context.setVariable("recipientName", dto.getBasicInfo().getTraineeName());
			String content = templateEngine.process("CourseContentTemplate", context);
			MimeMessagePreparator messagePreparator = mailSentCSRDrive(dto, content);
			return csrMailService.sentCsrMail(messagePreparator);
		}
	}

	private MimeMessagePreparator mailSentCSRDrive(TraineeDto dto, String content) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

			messageHelper.setFrom(helper.decrypt(chimpUserName));
			messageHelper.setTo(dto.getBasicInfo().getEmail());
			messageHelper.setSubject("X-workz");
			messageHelper.setText(content, true);
		};
		return messagePreparator;
	}

	@Override
	public boolean csrSmsSent(String name, String contactNo) {

		logger.info("SMS sent to {} with contact number {}", name, contactNo);
		csrSmsService.csrSMSSent(name, contactNo);
		return false;
	}

	private void sendAbsentEmailChimp(String traineeEmail, String name, String reason) {
		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("date", LocalDate.now());
		context.setVariable("reason", reason);
		String content = templateEngine.process("Absence", context);

		MimeMessagePreparator messagePreparator = mimeMessage -> {

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom("hareeshahr.xworkz@gmail.com");
			messageHelper.addCc("hr@x-workz.in");
			messageHelper.setTo(traineeEmail);

			messageHelper.setSubject("Absent Mail");
			messageHelper.setText(content, true);
		};
		chimpMailService.validateAndSendMail(messagePreparator);
	}

	@Override
	public Boolean sendAbsentMail(String email, String name, String reason) {
		System.err.println("email and name : " + email + ":" + name);
		if (email != null && name != null) {
			this.sendAbsentEmailChimp(email, name, reason);
			return true;
		} else {
			logger.warn("Email or name is null");
			return false;
		}
	}

	private boolean sendBulkMailToAttendanceNotification(List<String> recipients, List<TraineeDto> traineeDto) {
		Context context = new Context();

		context.setVariable("traineeDto", traineeDto);
		String content = templateEngine.process("AttendanceNotifficationTemplet", context);

		MimeMessagePreparator messagePreparator = mimeMessage -> {

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom("hareeshahr.xworkz@gmail.com");
			messageHelper.addTo(recipients.get(0));
			for (String recepent : recipients) {
				messageHelper.addCc(new InternetAddress(recepent));
			}
			messageHelper.setSubject("Attendance FollowUp");
			messageHelper.setText(content, true);
		};

		return chimpMailService.validateAndSendMailByMailIdDev(messagePreparator);
	}

	@Override
	public Boolean sendEmailNotificationForAttendanceFollowUp(List<TraineeDto> traineeDtos) {
		if (traineeDtos == null) {
			logger.warn("teamList or notificationStatus is null");
			return false;
		}

		List<Team> teamList;
		try {
			teamList = team.getTeam();
			String subject = "Attendance Follow Up Candidate Details";
			logger.debug("Sending email to {}: Subject: {},", teamList, subject);
			List<String> recipients = new ArrayList<String>();
			teamList.stream().filter(Objects::nonNull).forEach(e -> recipients.add(e.getEmail()));
			sendBulkMailToAttendanceNotification(recipients, traineeDtos);

			return true;
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;

	}
}
