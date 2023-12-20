package com.xworkz.dream.cache;

import java.util.List;

public interface ClientCacheService {

	 void addNewDtoToCache(String cacheName, String key, List<Object> list);
	 void addHRDetailsToCache(String cacheName, String key, List<Object> list);
	 void updateClientDetailsInCache(String cacheName,String key, List<List<Object>> values) throws IllegalAccessException;
}