package com.xworkz.dream.smsservice;

import java.util.Objects;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xworkz.dream.service.ChimpMailService;
import com.xworkz.dream.util.EncryptionHelper;
public class CSRSMSServiceImpl implements CSRSMSService {
	private String templateId="1607100000000295612";
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
	@Autowired
	private ChimpMailService chimpMailService;
	private Logger log = LoggerFactory.getLogger(CSRSMSService.class);

	@Override
	public boolean csrSMSSent() {
		String response = null;
		String status = null;
		System.out.println("Running sms service");

		try {
			String mobileNumber = "9900775088";
			if (Objects.nonNull(mobileNumber)) {

				String smsMessage = "Hi " + "Hareesha" + "," + "\n" + "Thanks for registering with X-workZ "
						+ " CSR Drive " + ". " + " Check  email for more details" + "." + "\n"
						+ "For queries, contact 9886971483/9886971480";
				;

				log.debug("smsType is :{} mobileNumber is :{} message is: {}", mobileNumber, smsMessage);

				response = chimpMailService.sendSMS(helper.decrypt(apiKey), helper.decrypt(smsUserName),
						helper.decrypt(sender), mobileNumber, smsMessage, helper.decrypt(smsType),
						helper.decrypt(route), helper.decrypt(templateId));

				log.info("SingleSMS Result is {}", response);
				JSONObject json = new JSONObject(response);
				status = json.getString("message");
				if (status.equals(helper.decrypt(smsSuccess))) {
					return true;
				} else {
					log.info("SingleSMS Result is {}", response);
					return false;
				}

			} else {
				log.info("SingleSMS Result is {}", response);
				return false;
			}

		} catch (Exception e) {
			log.error("\n\nMessage is {} and exception is {}\n\n\n\n\n", e.getMessage(), e);
			return false;
		}
	}

}
