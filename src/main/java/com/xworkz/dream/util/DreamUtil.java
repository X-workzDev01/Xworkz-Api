package com.xworkz.dream.util;

import java.io.IOException;

import javax.mail.MessagingException;

import freemarker.template.TemplateException;

public interface DreamUtil {
	
	public boolean sendOtptoEmail(String email, int otp);
	public int generateOTP();
	public String generateToken();
	public boolean sendCourseContent(String email,String name) throws MessagingException, IOException, TemplateException;
	
}
