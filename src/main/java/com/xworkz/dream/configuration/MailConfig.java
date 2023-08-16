package com.xworkz.dream.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

	@Value("${mail.smtpHost}")
	private String smtpHost;
	@Value("${mail.smtpPort}")
	private int smtpPort;
	@Value("${mail.userName}")
	private String userName;
	@Value("${mail.password}")
	private String password;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		// Set mail sender properties (host, port, username, password, etc.)

		// Set mail sender properties
		mailSender.setHost(smtpHost);
		mailSender.setPort(smtpPort);
		System.out.println(userName);
		System.out.println(password);
		//mailSender.setUsername("vinodamallappa@outlook.com");
		mailSender.setPassword("1973@Dad");
		// Email properties
		// Enable STARTTLS for secure email transmission
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");
       // props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");


		return mailSender;
	}
}
