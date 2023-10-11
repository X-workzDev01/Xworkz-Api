package com.xworkz.dream.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import com.xworkz.dream.configuration.MailConfig;
import com.xworkz.dream.util.EncryptionHelper;

@Service
public class ChimpmailServiceImpl implements ChimpMailService {

//	@Value("${mailChimp.apiKey}")
//	private String apiKeyMailChimp;
//	@Value("${mailChimp.SMSusername}")
//	private String smsUsername;
//	@Value("${mailChimp.route}")
//	private String rootType;
//	@Value("${mailChimp.templateId}")
//	private String templateIdMailChimp;
//	@Value("${mailChimp.sender}")
//	private String senderMailChimp;

	@Autowired
	private MailConfig config;
	@Autowired
	private EncryptionHelper encryptionHelper;

	@Autowired
	private SpringTemplateEngine templateEngine;

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
	public void mailService() {
//		Context context = new Context();
//		List<String> recipients = Arrays.asList("hareeshahareeshahr746@gmail.com", "keerthanahn.xworkz@gmail.com",
//				"xworkzdev4@gmail.com.com", "hareeshahr79@gmail.com");
//		int otp = util.generateOTP();
//
//		context.setVariable("onetimepass", otp);
//
//		String content = templateEngine.process("otpMailTemplate", context);
//
//		MimeMessagePreparator messagePreparator = mimeMessage -> {
//
//			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
//			for (String recepent : recipients) {
//				messageHelper.addTo(new InternetAddress(recepent));
//			}
//			messageHelper.setSubject("Your Otp is ");
//			messageHelper.setText(content, true);
//		};
//
//		this.validateAndSendMailByMailId(messagePreparator);
	}

	@Override
	public boolean sendOTPSMS() {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public boolean sendOTPSMS() {
//		String response = null;
//		int otp = util.generateOTP();
//		String status = null;
//		try {
//
//			String smsMessage = "Dear " + "Hareesha" + "," + '\n'
//					+ "Your one-time password (OTP) for X-workz Enquiry is " + otp + '\n' + '\n' + "See you soon,"
//					+ '\n' + "X-workz";
//
//			logger.debug("smsType is :{} mobileNumber is :{} message is: {}", "Text", "9900775088", smsMessage);
//
//			response = this.sendSMS(apiKeyMailChimp, smsUsername, senderMailChimp, "9900775088", smsMessage, "Text",
//					rootType, templateIdMailChimp);
//			logger.info("SingleSMS Result is {}", response);
//			JSONObject json = new JSONObject(response);
//			status = json.getString("message");
//			if (status.equals("sms sucessfully")) {
//				return true;
//			} else {
//				logger.info("SingleSMS Result is {}", response);
//				return false;
//			}
//
//		} catch (Exception e) {
//			logger.error("\n\nMessage is {} and exception is {}\n\n\n\n\n", e.getMessage(), e);
//			return false;
//		}
//	}

//	public String sendSMS(String apiKey, String username, String sender, String phone, String message, String smsType,
//			String route, String templateId) {
//		StringBuilder content = new StringBuilder();
//		String line;
//		try {
//			String requestUrl = "http://www.k3digitalmedia.co.in/websms/api/http/index.php?" + "&username="
//					+ URLEncoder.encode(username, "UTF-8") + "&apikey=" + URLEncoder.encode(apiKey, "UTF-8")
//					+ "&apirequest=" + URLEncoder.encode(smsType, "UTF-8") + "&route="
//					+ URLEncoder.encode(route, "UTF-8") + "&sender=" + URLEncoder.encode(sender, "UTF-8") + "&mobile="
//					+ URLEncoder.encode(phone, "UTF-8") + "&message=" + URLEncoder.encode(message, "UTF-8")
//					+ "&TemplateID=" + URLEncoder.encode(templateId, "UTF-8");
//			URL url = new URL(requestUrl);
//			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
//			uc.setDoOutput(true);
//			uc.setRequestMethod("POST");
//
//			DataOutputStream wr = new DataOutputStream(uc.getOutputStream());
//			wr.write(requestUrl.toString().getBytes());
//			BufferedReader bufferedReader = null;
//			if (uc.getResponseCode() == 200) {
//				logger.debug("Response code is {}", uc.getResponseCode());
//				bufferedReader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
//				logger.debug("Success URL is {}", uc);
//				logger.info("SMS Sent Succesfuly to +" + phone);
//				while ((line = bufferedReader.readLine()) != null) {
//					content.append(line).append("\n");
//				}
//				bufferedReader.close();
//				logger.debug("Content is {}", content);
//				return content.toString();
//			} else {
//				bufferedReader = new BufferedReader(new InputStreamReader(uc.getErrorStream()));
//				logger.debug("Fail contact number is {}", requestUrl.length());
//				logger.info("SMS Sent Faild to +" + phone);
//			}
//
//		} catch (Exception ex) {
//			logger.error(ex.getMessage());
//		}
//		return null;
//
//	}
}