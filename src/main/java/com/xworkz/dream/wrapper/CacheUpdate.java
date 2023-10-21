package com.xworkz.dream.wrapper;

import java.util.ArrayList;
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
	public  void updateCache(String cacheName, String key, List<Object> data) {
	    Cache cache = cacheManager.getCache(cacheName);
	    if (cache != null) {
	        ValueWrapper valueWrapper = cache.get(key);
	        List<Object> existingData = new ArrayList<>();
	        if (valueWrapper != null && valueWrapper.get() instanceof List) {
	            existingData = (List<Object>) valueWrapper.get();
	        }
	        existingData.addAll(data);
	        cache.put(key, existingData);
	    }
	}


}
