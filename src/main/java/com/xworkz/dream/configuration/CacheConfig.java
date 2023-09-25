package com.xworkz.dream.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
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
		caches.add(new ConcurrentMapCache("emailData"));
		caches.add(new ConcurrentMapCache("contactData"));
		caches.add(new ConcurrentMapCache("getDropdowns"));
		caches.add(new ConcurrentMapCache("getFollowUpStatusData"));
		caches.add(new ConcurrentMapCache("getFollowUpData"));
		caches.add(new ConcurrentMapCache("batchDetails"));
		caches.add(new ConcurrentMapCache("followUpDetails"));
		caches.add(new ConcurrentMapCache("followUpStatusDetails"));
		caches.add(new ConcurrentMapCache("byEmail"));
		caches.add(new ConcurrentMapCache("attendanceList"));

		cacheManager.setCaches(caches);
		return cacheManager;
	}
}