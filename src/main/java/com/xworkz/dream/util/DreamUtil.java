package com.xworkz.dream.util;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.mail.javamail.MimeMessagePreparator;

import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.Team;

import freemarker.template.TemplateException;

public interface DreamUtil {

	public boolean sendOtptoEmail(String email, int otp);

	public boolean sendNotificationToEmail(List<Team> teamList, List<StatusDto> notificationStatus);

	public int generateOTP();

	public String generateToken();

	public boolean sendCourseContent(String email, String name)
			throws MessagingException, IOException, TemplateException;

	public boolean sendWhatsAppLink(List<String> traineeEmail, String subject, String whatsAppLink);

	public boolean sms(TraineeDto dto);
	
	public boolean sendBirthadyEmail(String traineeEmail, String subject, String name);
}
