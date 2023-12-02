package com.xworkz.dream.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.ClientDto;

@Service
public class ClientCacheServiceImpl implements ClientCacheService {

	@Autowired
	private CacheManager cacheManager;

	private static final Logger log = LoggerFactory.getLogger(ClientCacheServiceImpl.class);

	@Override
	@SuppressWarnings("unchecked")
	public void addNewDtoToCache(String cacheName, String key, ClientDto dto) {
		// TODO Auto-generated method stub
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<ClientDto> existingList = (List<ClientDto>) valueWrapper.get();
				dto.setId(existingList.size() + 1);
				existingList.add(dto);
				System.out.println("client data added to cache:" + dto);
				((List<ClientDto>) valueWrapper.get()).add(dto);

			}
		}

	}

}
