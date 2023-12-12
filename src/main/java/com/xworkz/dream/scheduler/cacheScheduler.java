package com.xworkz.dream.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class cacheScheduler {
	Logger log = LoggerFactory.getLogger(cacheScheduler.class);

	@Scheduled(fixedRate = 86400000)
	@CacheEvict(value = { "emailData", "sheetsData", "register", "contactData", "getDropdowns", "batchDetails",
			"followUpDetails", "followUpStatusDetails" })
	private void clearCache() {
		log.info("Clear Cache after 24 Hours");
	}

}
