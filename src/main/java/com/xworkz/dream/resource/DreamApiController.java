package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xworkz.dream.service.DreamService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class DreamApiController {
	@Value("${login.sheetId}")
	private String id;
	private static final Logger log = LoggerFactory.getLogger(DreamApiController.class);

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
		log.info("Verifying email: {}", email);
		String verifyEmails = service.verifyEmails(email);
		ObjectMapper objectMapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = objectMapper.readValue(verifyEmails, Map.class);
		String reasons = (String) jsonMap.get("reason");
		if (reasons.equals("accepted_email")) {
			log.info("Verification result: {}", reasons);
			return reasons;
		} else {
			return reasons;
		}

	}

	@GetMapping("/cache")
	public String getList(@RequestParam String cacheName) throws IOException {
		log.info("Getting data from cache: {}", cacheName);
		Cache cache = manager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(id);
			if (valueWrapper != null) {
				Object cachedData = valueWrapper.get();
				log.info("Cached data: {}", cachedData);
				return null;
			}
		}
		return null;
	}

}
