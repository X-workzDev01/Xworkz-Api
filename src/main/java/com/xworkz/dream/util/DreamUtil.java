package com.xworkz.dream.util;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.Team;
import com.xworkz.dream.feesDtos.FeesDto;

import freemarker.template.TemplateException;

public interface DreamUtil {

	public boolean sendOtptoEmail(String email, int otp);

	public boolean sendNotificationToEmail(List<Team> teamList, List<FollowUpDto> notificationStatus);

	public boolean sendFeesNotificationToEmail(List<Team> teamList, List<FeesDto> notificationStatus);

	public int generateOTP();

	public String generateToken();

	public boolean sendCourseContent(String email, String name)
			throws MessagingException, IOException, TemplateException;

	public boolean sendWhatsAppLink(List<String> traineeEmail, String subject, String whatsAppLink);

	public boolean sms(TraineeDto dto);

	public boolean sendBirthadyEmail(String traineeEmail, String subject, String name);

	public boolean csrEmailSent(TraineeDto dto);

	public boolean csrSmsSent(String name,String contactNo);
	
	Boolean sendAbsentMail(String email,String name,String reason);
	
	Boolean sendEmailNotificationForAttendanceFollowUp(List<TraineeDto> dtos);

}
