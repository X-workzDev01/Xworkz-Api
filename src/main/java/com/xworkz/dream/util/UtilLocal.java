package com.xworkz.dream.util;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

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

import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.utils.Team;

import freemarker.template.TemplateException;

@Component
@Profile("local")
public class UtilLocal implements DreamUtil{

	@Value("${mail.smtpHost}")
	private String smtpHost;
	@Value("${mail.smtpPort}")
	private int smtpPort;
	@Value("${mail.userName}")
	private String userName;
	@Value("${mail.password}")
	private String password;
	

	private static final Logger logger = LoggerFactory.getLogger(UtilLocal.class);


    @Profile("local")
    @Override
    public boolean sendOtptoEmail(String email, int otp) {
        logger.debug("Sending local mail to {}", email);
        return true;
    }
    

	@Profile("local")
    @Override
	public  int generateOTP() {
	System.out.println("Local Otp");
	return 123456;
	}
	
	
	@Override
	public  String generateToken() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] tokenBytes = new byte[32];
		secureRandom.nextBytes(tokenBytes);
		return Base64.getEncoder().encodeToString(tokenBytes);
	}



	@Override
	public boolean sendCourseContent(String email, String name)
			throws MessagingException, IOException, TemplateException {

	public boolean sendNotificationToEmail(List<Team> teamList, List<String> candidateName,
			List<String> candidateEmail) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean sendNotificationToEmail(List<Team> teamList, List<StatusDto> notificationStatus) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean sendNotificationToEmail(List<Team> teamList, List<StatusDto> notificationStatus) {
		// TODO Auto-generated method stub
		return false;
	}


	


	

}
