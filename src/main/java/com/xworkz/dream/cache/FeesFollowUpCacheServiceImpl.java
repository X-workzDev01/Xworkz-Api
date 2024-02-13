package com.xworkz.dream.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class FeesFollowUpCacheServiceImpl implements FeesFollowUpCacheService {
	@Autowired
	private CacheManager cacheManager;
	private Logger log = LoggerFactory.getLogger(FeesFollowUpCacheServiceImpl.class);

	public void addNewFeesDetilesIntoCache(String cacheName, String key, List<Object> feesData) {
		feesData.remove(2);
		feesData.remove(11);
		feesData.remove(11);
		feesData.remove(21);
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {},", cacheName, key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				log.info("adding list to the cache {}:", feesData);
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				feesData.set(0, size + 1);
				feesData.remove(1);
				((List<List<Object>>) valueWrapper.get()).add(feesData);
			}
		}
	}

	public void addFeesFollowUpIntoCache(String cacheName, String key, List<Object> feesData) {
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {},", cacheName, key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				log.info("adding list to the cache {}:", feesData);
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				feesData.set(0, size + 1);
				feesData.remove(1);
				((List<List<Object>>) valueWrapper.get()).add(feesData);
			}
		}
	}

	@Override
	public void addEmailToCache(String cacheName, String key, String email) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			log.info("Email added into cache {} ", email);

			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<Object> addEmail = new ArrayList<Object>(Arrays.asList(email));
				((List<List<Object>>) valueWrapper.get()).add(addEmail);
			}
		}

	}

}
