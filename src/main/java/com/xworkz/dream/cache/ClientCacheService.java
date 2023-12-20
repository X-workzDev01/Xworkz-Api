package com.xworkz.dream.cache;

import java.util.List;

import com.xworkz.dream.dto.ClientDto;

public interface ClientCacheService {

	 void addNewDtoToCache(String cacheName, String key, List<Object> list);
	 void addHRDetailsToCache(String cacheName, String key, List<Object> list);
	 void updateClientDetailsInCache(String cacheName,String key, List<List<Object>> values) throws IllegalAccessException;
}