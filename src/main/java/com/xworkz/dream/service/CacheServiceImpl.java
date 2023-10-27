package com.xworkz.dream.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.wrapper.DreamWrapper;

@Component
public class CacheServiceImpl implements CacheService {
	@Autowired
	private DreamWrapper wrapper;
	private CacheManager cacheManager;

	@Autowired
	public CacheServiceImpl(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@SuppressWarnings("unchecked")
	public void updateCache(String cacheName, String key, List<Object> data) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				// cache.put(key, existingData);
				// adding single list to the cache
				((List<List<Object>>) valueWrapper.get()).add(data);

			}
		}
	}

	// retrieve cache data by email and updating to cache
	@SuppressWarnings("unchecked")
	public void getCacheDataByEmail(String cacheName, String key, String email, TraineeDto dto) {
		System.out.println(cacheName + ": " + " " + key + " " + email);
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			System.out.println("update data");
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				System.err.println();
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				List<Object> filterByEmail = ListOfItems.stream().filter(items -> items.get(2).equals(email))
						.findFirst().get();

				List<Object> list = wrapper.dtoToList(dto);
				list.add(filterByEmail);
				System.out.println("updating data in the cache:"+filterByEmail);
			}
		}
	}

}