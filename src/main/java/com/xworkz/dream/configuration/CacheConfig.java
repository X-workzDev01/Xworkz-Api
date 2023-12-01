package com.xworkz.dream.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.cache.CacheProperties.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

	@Bean
	@Primary
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();

		List<Cache> caches = new ArrayList<>();
		caches.add(new ConcurrentMapCache("sheetsData"));
		caches.add(new ConcurrentMapCache("emailData"));
		caches.add(new ConcurrentMapCache("register"));
		caches.add(new ConcurrentMapCache("contactData"));
		caches.add(new ConcurrentMapCache("getDropdowns"));
		caches.add(new ConcurrentMapCache("batchDetails"));
		caches.add(new ConcurrentMapCache("followUpDetails"));
		caches.add(new ConcurrentMapCache("followUpStatusDetails"));
		caches.add(new ConcurrentMapCache("clientInformation"));
		caches.add(new ConcurrentMapCache("hrDetails"));
		caches.add(new ConcurrentMapCache("hrFollowUpDetails"));
		cacheManager.setCaches(caches);
		return cacheManager;
	}

	@Bean
	public CacheManager alternateCacheManager() {
		// CaffeineCacheManager caffeineCacheManager=new
		// CaffeineCacheManager("ListOfClientDto","clientInformation");
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		// cacheManager.setCaches);
		List<Cache> caches = new ArrayList<>();

		cacheManager.setCaches(caches);
		return cacheManager;
	}
}
