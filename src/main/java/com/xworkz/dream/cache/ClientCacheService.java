package com.xworkz.dream.cache;

import java.util.List;

public interface ClientCacheService {

	abstract void addNewDtoToCache(String cacheName, String key, List<Object> list);
	abstract void addHRDetailsToCache(String cacheName, String key, List<Object> list);
}