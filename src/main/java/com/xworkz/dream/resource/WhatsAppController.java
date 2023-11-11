package com.xworkz.dream.resource;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.service.DreamService;
import com.xworkz.dream.service.WhatsAppService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class WhatsAppController {
	
	@Autowired
	private WhatsAppService whatsAppService;
	@Autowired
	private DreamService service;
	
	
	@PutMapping("/updateWhatsAppLink")
	public boolean updateWhatsAppLinkByCourseName(@RequestHeader String spreadsheetId, @RequestParam String cousreName,
			@RequestParam String whatsAppLink) throws IllegalAccessException, IOException {
		log.info("cousreName : whatsAppLink" + cousreName + " : " + whatsAppLink);
		return whatsAppService.updateWhatsAppLinkByCourseName(spreadsheetId, cousreName, whatsAppLink);
	}
	
	@GetMapping("/getWhatsAppLink")
	public String getWhatsAppLinkByCourseName(@RequestHeader String spreadsheetId, @RequestParam String courseName)
			throws IOException {
		ResponseEntity<BatchDetails> batchDetailsByCourseName = service.getBatchDetailsByCourseName(spreadsheetId,
				courseName);
		return batchDetailsByCourseName.getBody().getWhatsAppLink();
	}

	@GetMapping("/sendWhatsAppLink")
	public boolean mailWhatsAppLink(@RequestHeader String spreadsheetId, @RequestParam String courseName)
			throws IOException {
		boolean sendWhatsAppLink = whatsAppService.sendWhatsAppLink(spreadsheetId, courseName);
		return sendWhatsAppLink;
	}	
	

}
