package com.xworkz.dream.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.smsservice.CSRSMSService;
import com.xworkz.dream.util.DreamUtil;

@RestController
@RequestMapping("/")
public class EmailController {

	@Autowired
	private CSRSMSService service;
	@Autowired
	private DreamUtil util;

	@PostMapping("/sentmail")
	public ResponseEntity<String> sentmail() {
//		System.out.println(util.csrEmailSent("hareesha", "xworkzdev4@gmail.com", "4GEHarshi"));
//		service.csrSMSSent("Suhas","8050319217");
	System.out.println(util.csrEmailSent(new TraineeDto()));	
		

		return ResponseEntity.ok("mail sent sucessfully");
	}
	
	

}
