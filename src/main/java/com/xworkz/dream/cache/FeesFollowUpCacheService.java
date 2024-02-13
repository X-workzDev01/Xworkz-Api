package com.xworkz.dream.cache;

import java.util.List;

public interface FeesFollowUpCacheService {

	public void addNewFeesDetilesIntoCache(String cacheName, String key, List<Object> data);

	public void addEmailToCache(String cacheName, String key, String email);

}
