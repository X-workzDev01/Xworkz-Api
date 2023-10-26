package com.xworkz.dream.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheUpdate {

	private CacheManager cacheManager;

	@Autowired
	public CacheUpdate(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@SuppressWarnings("unchecked")
	public void updateCache(String cacheName, String key, List<Object> data) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				// cache.put(key, existingData);
				// adding single list to the cache
				((List<List<Object>>) valueWrapper.get()).add(data);

			}
		}
	}
	
	

}