package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.DreamService;
import com.xworkz.dream.service.WhatsAppService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class CreateBatchInfoController {
	
	
	Logger logger = LoggerFactory.getLogger(CreateBatchInfoController.class);

	@Autowired
	private DreamService service;
	@Autowired
	private WhatsAppService whatsAppService;
	
	@ApiOperation(value = "To register the upcoming batch details")
	@PostMapping("/batchInfo")
	public ResponseEntity<String> batchDetails(@RequestHeader String spreadsheetId, @RequestBody BatchDetailsDto dto,
			HttpServletRequest request) throws IOException, IllegalAccessException {
		logger.info("Registering trainee details: {}",dto);
		return service.saveDetails(spreadsheetId, dto, request);
	}
	
	@PutMapping("/updateWhatsAppLink")
	public boolean updateWhatsAppLinkByCourseName(@RequestHeader String spreadsheetId,@RequestParam String cousreName,@RequestParam String whatsAppLink) throws IllegalAccessException, IOException {
		logger.info("cousreName : whatsAppLink"+ cousreName +" : "+whatsAppLink);
		return whatsAppService.updateWhatsAppLinkByCourseName(spreadsheetId, cousreName, whatsAppLink);
	}
	
	@GetMapping("/getWhatsAppLink")
	public String getWhatsAppLinkByCourseName(@RequestHeader String spreadsheetId,
			@RequestParam String courseName) throws IOException {
		 ResponseEntity<BatchDetails> batchDetailsByCourseName = service.getBatchDetailsByCourseName(spreadsheetId, courseName);
		return batchDetailsByCourseName.getBody().getWhatsAppLink();

	}
	
	@GetMapping("/sendWhatsAppLink")
	public boolean  mailWhatsAppLink(@RequestHeader String spreadsheetId,
			@RequestParam String courseName) throws IOException {
		boolean sendWhatsAppLink = whatsAppService.sendWhatsAppLink(spreadsheetId, courseName);
		return sendWhatsAppLink;
		
	}
	
	
	

	
}
