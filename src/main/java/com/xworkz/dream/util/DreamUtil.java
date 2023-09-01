package com.xworkz.dream.util;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import com.xworkz.dream.dto.utils.Team;

import freemarker.template.TemplateException;

public interface DreamUtil {
	
	public boolean sendOtptoEmail(String email, int otp);
	public boolean sendNotificationToEmail(List<Team> teamList, List<String> candidateName, List<String> candidateEmail);
	public int generateOTP();
	public String generateToken();
	public boolean sendCourseContent(String email,String name) throws MessagingException, IOException, TemplateException;
	
}
