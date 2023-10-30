package com.xworkz.dream.service;

import java.util.Iterator;
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

	private void FindAndSetValue(String key, String email, FollowUpDto dto, Cache cache,
			List<List<Object>> ListOfItems) {
		if (email != null) {
			FollowUpDto followUpDto = ListOfItems.stream()
					.filter(list -> list.size() > 2 && list.get(2).toString().equalsIgnoreCase(email))
					.map(list -> wrapper.listToFollowUpDTO(list)).findFirst() // Get the first matching FollowUpDto or
																				// Optional<FollowUpDto>
					.orElse(null); // Handle the case when no match is found

			// Update the email from the dto
			if (followUpDto != null) {
				followUpDto.getBasicInfo().setEmail(dto.getBasicInfo().getEmail());
				followUpDto.setAdminDto(dto.getAdminDto());
				cache.put(key, followUpDto);
			} else {
				logger.debug("followUpDto is null");
			}
		} else {
			logger.debug("email is null");
		}
	}

	@Override
	public void updateFollowUpStatus(String cacheName, String key, StatusDto statusDto) {
		Cache cache = cacheManager.getCache(cacheName);
		String email = statusDto.getBasicInfo().getEmail();
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
//				@SuppressWarnings("unchecked")
//				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
//				FollowUpDto followUpDto = ListOfItems.stream()
//						.filter(list -> list.size() > 2 && list.get(2).toString().equalsIgnoreCase(email))
//						.map(list -> wrapper.listToFollowUpDTO(list)).findFirst() // Get the first matching FollowUpDto
//																					// or
//																					// Optional<FollowUpDto>
//						.orElse(null); // Handle the case when no match is found
//				if (followUpDto != null) {
//					followUpDto.setCurrentStatus(statusDto.getAttemptStatus());
//					followUpDto.setJoiningDate(statusDto.getJoiningDate());
//					cache.put(key, followUpDto);
//				}
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
				((List<List<Object>>) valueWrapper.get()).add(data);
			}
		}
	}

}