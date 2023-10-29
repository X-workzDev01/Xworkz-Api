package com.xworkz.dream.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.service.CacheServiceImpl;

@RestController
@RequestMapping("/api")
public class CacheController {

	@Autowired
	private CacheManager cacheManager;

	private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);

	@Autowired
	public CacheController(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@DeleteMapping("/evict")
	public String evictCacheValue(@RequestHeader String spreadSheedId, @RequestParam String cacheName,
			@RequestParam String cacheKey) {
		Cache cache = cacheManager.getCache(cacheName);

		if (cache != null) {
			cache.evict(cacheKey);
			logger.debug("cache is cleared");
			return "Cache value with key '" + cacheKey + "' in cache '" + cacheName + "' has been evicted.";
		} else {
			return "Cache '" + cacheName + "' not found.";
		}
	}

}
