package com.xworkz.dream.resource;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.service.BatchService;
import com.xworkz.dream.service.WhatsAppService;

@RestController
@RequestMapping("/api")

public class WhatsAppController {

	@Autowired
	private WhatsAppService whatsAppService;
	@Autowired
	private BatchService service;

	private static final Logger log = LoggerFactory.getLogger(WhatsAppController.class);

	@PutMapping("/updateWhatsAppLink")
	public boolean updateWhatsAppLinkByCourseName(@RequestHeader String spreadsheetId, @RequestParam String courseName,
			@RequestParam String whatsAppLink) throws IllegalAccessException, IOException {
		log.info("Updating WhatsApp link - SpreadsheetId: {}, CourseName: {}, WhatsAppLink: {}", spreadsheetId,
				courseName, whatsAppLink);
		return whatsAppService.updateWhatsAppLinkByCourseName(spreadsheetId, courseName, whatsAppLink);
	}

	@GetMapping("/getWhatsAppLink")
	public String getWhatsAppLinkByCourseName(@RequestHeader String spreadsheetId, @RequestParam String courseName)
			throws IOException {
		log.info("Getting WhatsApp link - SpreadsheetId: {}, CourseName: {}", spreadsheetId, courseName);
		BatchDetailsDto batchDetailsByCourseName = service.getBatchDetailsByCourseName(spreadsheetId, courseName);
		return batchDetailsByCourseName.getWhatsAppLink();

	}

	@GetMapping("/sendWhatsAppLink")
	public String mailWhatsAppLink(@RequestHeader String spreadsheetId, @RequestParam String courseName)
			throws IOException {
		log.info("Sending WhatsApp link - SpreadsheetId: {}, CourseName: {}", spreadsheetId, courseName);
		Boolean sendWhatsAppLink = whatsAppService.sendWhatsAppLink(spreadsheetId, courseName);
		if(sendWhatsAppLink==true) {
			return "Send WhatsAppLink successfully";
		}else {
			return "WhatsAppLink allreday Sent";
		}
		
	}

	@PostMapping("/updateWhatsAppLink")
	public String updateWhatsAppLink(@RequestParam String courseName, @RequestParam String whatsAppLink)
			throws IllegalAccessException, IOException {
		Boolean updateWhatsAppLinkByBatchName = whatsAppService.updateWhatsAppLinkByBatchName(courseName, whatsAppLink);
		if(updateWhatsAppLinkByBatchName==true) {
			return "WhatsAppLink Update successfully";
		}else {
		return " whatsAppLink Not update";
		}
	}

}
