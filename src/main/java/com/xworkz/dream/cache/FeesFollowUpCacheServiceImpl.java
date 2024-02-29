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

	@SuppressWarnings("unchecked")
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
				List<List<Object>> listOfCacheData = ((List<List<Object>>) valueWrapper.get());
				log.info("adding list to the cache {}:", feesData);
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				feesData.set(0, size + 1);
				((List<List<Object>>) valueWrapper.get()).add(feesData);
				if (listOfCacheData.get(0).size() < 2) {
					listOfCacheData.remove(0);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void addFeesFollowUpIntoCache(String cacheName, String key, List<Object> followUpData) {
		Cache cache = cacheManager.getCache(cacheName);

		log.info("cache name: {}, cache key: {},", cacheName, key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				log.info("adding list to the cache {}:", followUpData);
				List<List<Object>> listOfCacheData = ((List<List<Object>>) valueWrapper.get());
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				followUpData.set(0, size + 1);
				((List<List<Object>>) valueWrapper.get()).add(followUpData);
				if (listOfCacheData.get(0).size() < 2) {
					listOfCacheData.remove(0);
				}
			} 
		}
	}

	public void updateCacheIntoFeesDetils(String cacheName, String key, String email, List<Object> feesUpdateData) {
		if (feesUpdateData.size() > 21) {
			feesUpdateData.remove(2);
			feesUpdateData.remove(11);
			feesUpdateData.remove(11);
			feesUpdateData.remove(21);
		}
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1;

				for (int i = 0; i < ListOfItems.size(); i++) {
					List<Object> items = ListOfItems.get(i);
					if (items.get(2).equals(email)) {
						matchingIndex = i;
						break;
					}
				}
				if (matchingIndex >= 0) {
					ListOfItems.set(matchingIndex, feesUpdateData);
					log.info("Updated cache data for email: {}", email);
				}

			} else {
				log.debug("Data not found in the cache for the specified email: {}", email);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addEmailToCache(String cacheName, String key, String email) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			log.info("Email added into cache {} ", email);

			ValueWrapper valueWrapper = cache.get(key);

			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<Object> addEmailIntoList = new ArrayList<Object>(Arrays.asList(email));
				((List<List<Object>>) valueWrapper.get()).add(addEmailIntoList);
			} else {
				evictAllCacheValues(cacheName);
			}
		}

	}

	public void evictAllCacheValues(String cacheName) {
		cacheManager.getCache(cacheName).clear();
	}

	public void updateFeesCacheIntoEmail(String cacheName, String key, String oldEmail, String newEmail) {

		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1;
				for (int i = 0; i < ListOfItems.size(); i++) {
					List<Object> items = ListOfItems.get(i);
					if (items.get(0).equals(oldEmail)) {
						matchingIndex = i;
						break;
					}
				}
				if (matchingIndex >= 0) {
					ListOfItems.set(matchingIndex, Arrays.asList(newEmail));
					log.info("Updated cache data for email: {}", newEmail);
				}
			} else {
				log.debug("Data not found in the cache for the specified email: {}", oldEmail);
			}
		}
	}

	public void updateCacheIntoFeesFollowUp(String cacheName, String key, String email, List<Object> feesUpdateData) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1;

				for (int i = 0; i < ListOfItems.size(); i++) {
					List<Object> items = ListOfItems.get(i);
					if (items.get(1).equals(email)) {
						matchingIndex = i;
						break;
					}
				}
				if (matchingIndex >= 0) {
					ListOfItems.set(matchingIndex, feesUpdateData);
					log.info("Updated cache data for email: {}", email);
				}

			} else {
				log.debug("Data not found in the cache for the specified email: {}", email);
			}
		}
	}

}
