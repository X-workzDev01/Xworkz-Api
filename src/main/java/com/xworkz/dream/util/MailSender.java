package com.xworkz.dream.util;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import org.apache.naming.factory.SendMailFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailSender {
	
	@Value("${mail.smtpHost}")
	private  String smtpHost;
	@Value("${mail.smtpPort}")  
    private  int smtpPort;
	@Value("${mail.userName}")  
    private  String userName;
	@Value("${mail.password}")  
    private  String password;
	

	public  boolean sendOtptoEmail(String email, int otp) {
		String subject = "OTP for Login";
		String body = "Hi , Your Otp is " + otp + "/n Thank You!";
		System.out.println(subject);
		System.out.println(body);
		return sendEmail(email, subject, body);
		
	}
	
	public  boolean sendEmail(String email, String subject, String body) {
     

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
            System.out.println(userName);
            message.setFrom(new InternetAddress(userName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            message.setText(body);

            // Send email
            Transport.send(message);

            return true; // Email sent successfully
        } catch (MessagingException e) {
            e.printStackTrace();
            return false; // Failed to send email
        }
    }
	

}
