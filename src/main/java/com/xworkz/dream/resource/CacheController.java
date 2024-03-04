package com.xworkz.dream.resource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	private static final Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);

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
			log.info("Cache value with key '{}' in cache '{}' has been evicted.", cacheKey, cacheName);
			return "Cache value with key '" + cacheKey + "' in cache '" + cacheName + "' has been evicted.";
		} else {
			log.warn("Cache '{}' not found.", cacheName);
			return "Cache '" + cacheName + "' not found.";
		}
	}

	@GetMapping("/getByCacheName")
	public ResponseEntity<List<List<Object>>> getByCacheName(String cacheName, @RequestHeader String cacheKey) {
		Cache cache = cacheManager.getCache(cacheName);

		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(cacheKey);
			if (valueWrapper != null) {
				@SuppressWarnings("unchecked")
				List<List<Object>> cachedData = (List<List<Object>>) valueWrapper.get();
				log.info("Retrieved data from cache '{}': {}", cacheName, cachedData);
				return ResponseEntity.ok(cachedData);

			}
		}
		return null;
	}

	@PostMapping("/clear/{cacheName}")
	public String clearCache(@PathVariable String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);

		if (cache != null) {
			cache.clear();
			log.info("Cache '{}' has been cleared.", cacheName);
			return "Cache '" + cacheName + "' has been cleared.";
		} else {
			log.warn("Cache '{}' not found.", cacheName);
			return "Cache '" + cacheName + "' not found.";
		}
	}

}
