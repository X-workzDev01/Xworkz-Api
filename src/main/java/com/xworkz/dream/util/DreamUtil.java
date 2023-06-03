package com.xworkz.dream.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class DreamUtil {
	
	public static int generateOTP() {
		  // Generate a random OTP
        int otpLength = 6;
        int otpMinValue = 100000;
        int otpMaxValue = 999999;
        Random random = new Random();

        return otpMinValue + random.nextInt(otpMaxValue - otpMinValue + 1);
	}
	
	public static String generateToken() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] tokenBytes = new byte[32];
		secureRandom.nextBytes(tokenBytes);
		return Base64.getEncoder().encodeToString(tokenBytes);
	}


}
