package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.BatchDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.DreamService;

import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class DreamApiController {
	@Value("${login.sheetId}")
	private String id;
	Logger logger = LoggerFactory.getLogger(DreamApiController.class);

	private DreamService service;

	@Autowired
	private CacheManager manager;

	@Autowired
	public DreamApiController(DreamService service) {
		this.service = service;
	}


	@ApiOperation(value = "To verifay the email")
	@GetMapping("/verify-email")
	public String verifydEmails(@RequestParam String email) throws IOException {
		String verifyEmails = service.verifyEmails(email);
		ObjectMapper objectMapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = objectMapper.readValue(verifyEmails, Map.class);
		String reasons = (String) jsonMap.get("reason");
		if (reasons.equals("accepted_email")) {
			return reasons;
		} else {
			return reasons;
		}

	}

	
	@GetMapping("/cache")
	public  String getList(@RequestParam String cacheName) throws IOException {

		Cache cache = manager.getCache(cacheName);



		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(id);

			if (valueWrapper != null) {
				Object cachedData =  valueWrapper.get();
				return null;
			}
		}
		return null;
	}

}
