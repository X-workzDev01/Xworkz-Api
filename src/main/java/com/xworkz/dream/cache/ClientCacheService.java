package com.xworkz.dream.cache;

import java.util.List;

public interface ClientCacheService {

	void addNewDtoToCache(String cacheName, String key, List<Object> list);

	void addHRDetailsToCache(String cacheName, String key, List<Object> list);

	void updateClientDetailsInCache(String cacheName, String key, List<Object> values);

	void updateHrDetailsInCache(String cacheName, String key, List<Object> list);

	void addHrFollowUpToCache(String cacheName, String key, List<Object> list);

	void addToCache(String cacheName, String key, String value);

	void updateCache(String cacheName, String key, String oldvalue, String newValue);
}