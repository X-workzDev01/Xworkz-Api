package com.xworkz.dream.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.xworkz.dream.util.EncryptionHelper;

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
	@Value("${mailChimp.userName}")
	private String chimpUserName;
	@Value("${mailChimp.password}")
	private String chimpPassword;
	@Value("${mailChimp.smtpHostChimp}")
	private String smtpHostChimp;
	@Autowired
	private EncryptionHelper helper;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(smtpHost);
		mailSender.setPort(smtpPort);
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		return mailSender;
	}

	@Bean
	public JavaMailSender getMailSenderDev() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername("hareeshahr.xworkz@gmail.com");
		mailSender.setPassword("vtgf meha ujpx ficd");

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.starttls.enable", "true");
		javaMailProperties.put("mail.smtp.auth", "true");
		javaMailProperties.put("mail.transport.protocol", "smtp");
		javaMailProperties.put("mail.debug", "true");
		javaMailProperties.put("mail.smtp.ssl.trust", "*"); // Trust Gmail SSL certificate
		javaMailProperties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
		mailSender.setJavaMailProperties(javaMailProperties);
		return mailSender;

	}

	@Bean
	public JavaMailSender getMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		mailSender.setHost(helper.decrypt(smtpHostChimp));
		mailSender.setPort(587);
		mailSender.setUsername(helper.decrypt(chimpUserName));
		mailSender.setPassword(helper.decrypt(chimpPassword));
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.starttls.enable", "true");
		javaMailProperties.put("mail.smtp.auth", "true");
		javaMailProperties.put("mail.transport.protocol", "smtp");
		javaMailProperties.put("mail.debug", "true");
		javaMailProperties.put("mail.smtp.ssl.trust", "*"); // Trust Gmail SSL certificate
		javaMailProperties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
		mailSender.setJavaMailProperties(javaMailProperties);
		return mailSender;
	}
}
