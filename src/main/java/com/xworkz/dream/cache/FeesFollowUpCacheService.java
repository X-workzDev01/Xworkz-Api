package com.xworkz.dream.cache;

import java.util.List;

public interface FeesFollowUpCacheService {

	void addNewFeesDetilesIntoCache(String cacheName, String key, List<Object> data);

	void addFeesFollowUpIntoCache(String cacheName, String key, List<Object> followUpData);

	void updateCacheIntoFeesDetils(String cacheName, String key, String email, List<Object> feesUpdateData)
			throws IllegalAccessException;

	void addEmailToCache(String cacheName, String key, String email);
}
