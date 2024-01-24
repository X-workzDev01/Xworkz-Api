package com.xworkz.dream.wrapper;


import org.springframework.beans.factory.annotation.Autowired;

import com.xworkz.dream.smsservice.CSRSMSService;

public class EmailSevice {
	@Autowired
	private CSRSMSService csrsmsService;

	public boolean smsSent() {
		System.out.println("SMS Sent Running");
		return csrsmsService.csrSMSSent();
	}

	public static void main(String[] args) {
		EmailSevice emailSevice = new EmailSevice();
		emailSevice.smsSent();
	}

}
