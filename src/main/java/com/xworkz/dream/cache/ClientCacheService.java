package com.xworkz.dream.cache;

import com.xworkz.dream.dto.ClientDto;

public interface ClientCacheService {
	
	abstract void addNewDtoToCache(String cacheName, String key, ClientDto dto);
}