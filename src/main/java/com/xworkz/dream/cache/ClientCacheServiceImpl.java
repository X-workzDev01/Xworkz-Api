package com.xworkz.dream.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class ClientCacheServiceImpl implements ClientCacheService {

	@Autowired
	private CacheManager cacheManager;

	private static final Logger log = LoggerFactory.getLogger(ClientCacheServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public void addNewDtoToCache(String cacheName, String key, List<Object> data) {
		
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {},",cacheName,key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				// cache.put(key, existingData);
				// adding single list to the cache
				log.info("adding list to the cache {}:",data);
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				data.set(0, size + 1);
				data.remove(1);
				((List<List<Object>>) valueWrapper.get()).add(data);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addHRDetailsToCache(String cacheName, String key, List<Object> data) {

		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {},",cacheName,key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				// cache.put(key, existingData);
				// adding single list to the cache
				log.info("adding list to the cache {}:",data);
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				data.set(0, size + 1);
				((List<List<Object>>) valueWrapper.get()).add(data);
			}
		}
	}

}
