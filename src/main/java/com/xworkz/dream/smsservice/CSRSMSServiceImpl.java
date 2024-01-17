package com.xworkz.dream.smsservice;

import java.util.Objects;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.ChimpMailService;
import com.xworkz.dream.util.EncryptionHelper;

public class CSRSMSServiceImpl implements CSRSMSService {
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
	@Autowired
	private ChimpMailService chimpMailService;
	private Logger log = LoggerFactory.getLogger(CSRSMSService.class);

	@Override
	public boolean csrSMSSent(TraineeDto dto) {
		String response = null;
		String status = null;
		try {
			String mobileNumber = dto.getBasicInfo().getContactNumber().toString();
			if (Objects.nonNull(mobileNumber)) {

				String smsMessage = "Hi " + dto.getBasicInfo().getTraineeName().toString() + "," + "\n"
						+ "Thanks for registering with X-workZ " + " CSR Drive " + "." + "Check  email for more details"
						+ "." + "\n" + " For queries, contact 9886971483/9886971480" + ".";
				;

				log.debug("smsType is :{} mobileNumber is :{} message is: {}", mobileNumber, smsMessage);

				response = chimpMailService.sendSMS(helper.decrypt(apiKey), helper.decrypt(smsUserName),
						helper.decrypt(sender), mobileNumber, smsMessage, helper.decrypt(smsType),
						helper.decrypt(route), helper.decrypt(templateIdEnquiry));

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
