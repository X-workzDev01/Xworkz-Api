package com.xworkz.dream.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.wrapper.DreamWrapper;

@Component
public class CacheServiceImpl implements CacheService {
	@Autowired
	private DreamWrapper wrapper;
	private CacheManager cacheManager;

	private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);

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
				int size=(((List<List<Object>>) valueWrapper.get()).size());
				System.out.println("size of cache is:");
				data.set(0, size+1);
				
				((List<List<Object>>) valueWrapper.get()).add(data);
			}
		}
	}

	// retrieve cache data by email and updating to cache
	@SuppressWarnings("unchecked")
	public void getCacheDataByEmail(String cacheName, String key, String email, TraineeDto dto)
			throws IllegalAccessException {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				List<List<Object>> listOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1; // Initialize to -1, indicating not found initially

				for (int i = 0; i < listOfItems.size(); i++) {
					List<Object> items = listOfItems.get(i);
					if (items.get(2).equals(email)) {
						matchingIndex = i; // Set the index when a match is found
						break; // Exit the loop once a match is found
					}
				}
				List<Object> list = wrapper.extractDtoDetails(dto);

				if (matchingIndex >= 0) {

					listOfItems.set(matchingIndex, list);
				}

			}
		}
	}

	@Override
	public void updateCacheFollowUp(String cacheName, String key, String email, FollowUpDto dto) throws IllegalAccessException {

		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1; // Initialize to -1, indicating not found initially

				for (int i = 0; i < ListOfItems.size(); i++) {
					List<Object> items = ListOfItems.get(i);
					if (items.get(2).equals(email)) {
						matchingIndex = i; // Set the index when a match is found
						break; // Exit the loop once a match is found
					}
				}
				List<Object> list = wrapper.extractDtoDetails(dto);

				if (matchingIndex >= 0) {

					ListOfItems.set(matchingIndex, list);
				}

			} else {
				logger.debug("Data not found in the cache for the specified email: " + email);
			}
		}
	}

	

	@Override
	public void updateFollowUpStatus(String cacheName, String key, StatusDto statusDto) throws IllegalAccessException {
		Cache cache = cacheManager.getCache(cacheName);
		String email = statusDto.getBasicInfo().getEmail();
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1; // Initialize to -1, indicating not found initially

				for (int i = 0; i < ListOfItems.size(); i++) {
					List<Object> items = ListOfItems.get(i);
					if (items.get(2).equals(email)) {
						matchingIndex = i; // Set the index when a match is found
						break; // Exit the loop once a match is found
					}
				}
				List<Object> list = wrapper.extractDtoDetails(statusDto);

				if (matchingIndex >= 0) {

					ListOfItems.set(matchingIndex, list);
				}

			} else {
				logger.debug("Data not found in the cache for the specified email: " + email);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void updateFollowUpStatusInCache(String cacheName, String key, List<Object> data) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				// cache.put(key, existingData);
				// adding single list to the cache
				if (data.size() > 5) {
					data.remove(5); // Removes the element at index 5
				}
				if (data.size() > 4) {
					data.remove(4); // Removes the element at index 4
				}
				((List<List<Object>>) valueWrapper.get()).add(data);
			}
		}
	}

}