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
				((List<List<Object>>) valueWrapper.get()).add(data);

			}
		}
	}

	// retrieve cache data by email and updating to cache
	@SuppressWarnings("unchecked")
	public void getCacheDataByEmail(String cacheName, String key, String email, TraineeDto dto) {

		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				List<Object> filterByEmail = ListOfItems.stream().filter(items -> items.get(2).equals(email))
						.findFirst().get();

				if (filterByEmail != null) {
					// Update the filterByEmail list with values from the TraineeDto
					setValuesToDto(key, dto, cache, filterByEmail);
				} else {
					logger.debug("Data not found in the cache for the specified email: " + email);
				}
			}
		}
	}

	private void setValuesToDto(String key, TraineeDto dto, Cache cache, List<Object> filterByEmail) {
		filterByEmail.set(0, dto.getId()); // Update the ID
		filterByEmail.set(1, dto.getBasicInfo()); // Update BasicInfoDto
		filterByEmail.set(2, dto.getEducationInfo()); // Update EducationInfoDto
		filterByEmail.set(3, dto.getCourseInfo()); // Update CourseDto
		filterByEmail.set(4, dto.getOthersDto()); // Update OthersDto
		filterByEmail.set(5, dto.getAdminDto()); // Update AdminDto
		cache.put(key, filterByEmail);
	}

	@Override
	public void updateCacheFollowUp(String cacheName, String key, String email, FollowUpDto dto) {

		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				FindAndSetValue(key, email, dto, cache, ListOfItems);
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
				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				FollowUpDto followUpDto = ListOfItems.stream()
						.filter(list -> list.size() > 2 && list.get(2).toString().equalsIgnoreCase(email))
						.map(list -> wrapper.listToFollowUpDTO(list)).findFirst() // Get the first matching FollowUpDto
																					// or
																					// Optional<FollowUpDto>
						.orElse(null); // Handle the case when no match is found
				if (followUpDto != null) {
					followUpDto.setCurrentStatus(statusDto.getAttemptStatus());
					followUpDto.setJoiningDate(statusDto.getJoiningDate());
					cache.put(key, followUpDto);
				}
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