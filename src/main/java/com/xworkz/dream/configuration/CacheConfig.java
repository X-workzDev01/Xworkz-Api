package com.xworkz.dream.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();

		List<Cache> caches = new ArrayList<>();
		caches.add(new ConcurrentMapCache("sheetsData"));
		caches.add(new ConcurrentMapCache("followUpEmailRange"));
		caches.add(new ConcurrentMapCache("emailData"));
		caches.add(new ConcurrentMapCache("readFirst"));
		caches.add(new ConcurrentMapCache("register"));
		caches.add(new ConcurrentMapCache("contactData"));
		caches.add(new ConcurrentMapCache("getDropdowns"));
		caches.add(new ConcurrentMapCache("getFeesEmail"));
		caches.add(new ConcurrentMapCache("getEmailList"));
		caches.add(new ConcurrentMapCache("getFeesDetails"));
		caches.add(new ConcurrentMapCache("feesFollowUpEmailRange"));
		caches.add(new ConcurrentMapCache("getFeesFolllowUpdata"));
		caches.add(new ConcurrentMapCache("batchDetails"));
		caches.add(new ConcurrentMapCache("clientInformation"));
		caches.add(new ConcurrentMapCache("hrDetails"));
		caches.add(new ConcurrentMapCache("hrFollowUpDetails"));
		caches.add(new ConcurrentMapCache("attendanceData"));
		caches.add(new ConcurrentMapCache("batchAttendanceData"));
		caches.add(new ConcurrentMapCache("usnNumber"));
		caches.add(new ConcurrentMapCache("alternativeNumber"));
		caches.add(new ConcurrentMapCache("uniqueNumber"));
		caches.add(new ConcurrentMapCache("getFollowUpDetails"));
		caches.add(new ConcurrentMapCache("getFollowUpStatusDetails"));
		caches.add(new ConcurrentMapCache("getClientDropDown"));
		caches.add(new ConcurrentMapCache("getClientEmail"));
		caches.add(new ConcurrentMapCache("getClientContactNumbers"));
		caches.add(new ConcurrentMapCache("getClientWebsite"));
		caches.add(new ConcurrentMapCache("getClientName"));
		caches.add(new ConcurrentMapCache("getListOfHrEmail"));
		caches.add(new ConcurrentMapCache("getListOfContactNumber"));
		cacheManager.setCaches(caches);
		return cacheManager;
	}

}
