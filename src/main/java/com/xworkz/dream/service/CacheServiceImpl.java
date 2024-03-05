package com.xworkz.dream.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.wrapper.DreamWrapper;

@Component
public class CacheServiceImpl implements CacheService {
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private CacheManager cacheManager;

	private static final Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);

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
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				data.set(0, size + 1);
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
					log.info("Updated cache data for email: {}", email);

				}

			}
		}
	}

	@Override
	public void updateCacheFollowUp(String cacheName, String key, String email, FollowUpDto dto)
			throws IllegalAccessException {

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
				list.remove(4);

				if (matchingIndex >= 0) {

					ListOfItems.set(matchingIndex, list);
					log.info("Updated cache data for email: {}", email);
				}

			} else {
				log.debug("Data not found in the cache for the specified email: {}", email);
			}
		}
	}

	@Override
	public void updateFollowUpStatus(String cacheName, String key, String email, List<Object> statusData) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1;

				for (int i = 0; i < ListOfItems.size(); i++) {
					List<Object> items = ListOfItems.get(i);
					if (items.get(2).equals(email)) {
						matchingIndex = i;
						break;
					}
				}
				if (matchingIndex >= 0) {
					statusData.remove(4);
					statusData.remove(4);
					statusData.add("NA");
					statusData.remove(11);
					ListOfItems.set(matchingIndex, statusData);
					log.info("Updated cache data for email: {}", email);
				}

			} else {
				log.debug("Data not found in the cache for the specified email: {}", email);
			}
		}
	}

	public void EmailUpdate(String cacheName, String key, String oldEmail, String newEmail) {

		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1;
				for (int i = 0; i < ListOfItems.size(); i++) {
					List<Object> items = ListOfItems.get(i);
					if (items.get(0).equals(oldEmail)) {
						matchingIndex = i;
						break;
					}
				}
				if (matchingIndex >= 0) {
					ListOfItems.set(matchingIndex, Arrays.asList(newEmail));
					log.info("Updated cache data for email: {}", newEmail);
				}
			} else {
				log.debug("Data not found in the cache for the specified email: {}", oldEmail);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void addToFollowUpStatusCache(String cacheName, String key, List<Object> data) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				if (data.size() > 5) {
					data.remove(5);
				}
				if (data.size() > 4) {
					data.remove(4);
				}
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				data.set(0, size + 1);
				((List<List<Object>>) valueWrapper.get()).add(data);
				log.info("Updated cache for key: {}", key);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void updateCourseCache(String cacheName, String key, List<Object> data) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				data.set(0, size + 1);
				((List<List<Object>>) valueWrapper.get()).add(data);
				log.info("Updated cache for key: {}", key);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addFollowUpToCache(String cacheName, String key, List<Object> data) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				// cache.put(key, existingData);
				// adding single list to the cache
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				data.set(0, size + 1);
				data.remove(4);
				((List<List<Object>>) valueWrapper.get()).add(data);
				log.info("Updated cache for key: {}", key);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addEmailToCache(String cacheName, String spreadSheetId, String email) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			log.info("Email added into cache {} ", email);

			ValueWrapper valueWrapper = cache.get(spreadSheetId);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<Object> EmailList = new ArrayList<Object>(Arrays.asList(email));
				((List<List<Object>>) valueWrapper.get()).add(EmailList);
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void addContactNumberToCache(String cacheName, String spreadSheetId, Long contactNumber) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {

			log.info("Contact number added into cache: {}", contactNumber);
			@SuppressWarnings("unchecked")
			ValueWrapper valueWrapper = cache.get(spreadSheetId);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<Object> contactNumbers = new ArrayList<Object>(Arrays.asList(contactNumber));
				((List<List<Object>>) valueWrapper.get()).add(contactNumbers);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void getCacheDataByEmail(String cacheName, String key, String oldEmail, String newEmail)
			throws IllegalAccessException {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				List<List<Object>> listOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1;
				for (int i = 0; i < listOfItems.size(); i++) {
					List<Object> items = listOfItems.get(i);
					if (items.get(0).equals(oldEmail)) {
						matchingIndex = i;
						break;
					}
				}

				if (matchingIndex >= 0) {

					listOfItems.set(matchingIndex, Stream.of(newEmail).collect(Collectors.toList()));
					log.info("Updated cache for key: {} with new email: {}", key, newEmail);

				}

			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addAttendancdeToCache(String cacheName, String key, List<Object> data) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {

			ValueWrapper valueWrapper = cache.get(key);
			List<List<Object>> list = ((List<List<Object>>) valueWrapper.get());
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				int size = (((List<List<Object>>) valueWrapper.get()).size());

				data.set(0, size + 1);
				((List<List<Object>>) valueWrapper.get()).add(data);
				if (list.get(0).size() <= 1) {
					list.remove(0);
				}

				log.info("Updated cache for key: {}", key);
			}
		}
	}

	@Override
	public void updateCacheAttendancde(String cacheName, String key, Integer id, AttendanceDto dto)
			throws IllegalAccessException {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1;

				for (int i = 0; i < ListOfItems.size(); i++) {
					List<Object> items = ListOfItems.get(i);
					Integer getId = Integer.valueOf(items.get(1).toString());
					if (getId.equals(id)) {
						matchingIndex = i;
						break;
					}
				}
				List<Object> list = wrapper.extractDtoDetails(dto);
				if (matchingIndex >= 0) {
					list.remove(4);
					ListOfItems.set(matchingIndex, list);

					log.info("Updated cache data for id: {}", id);
				}

			} else {
				log.debug("Data not found in the cache for the specified id: {}", id);
			}
		}
	}

	@Override
	public void updateCacheBatch(String cacheName, String key, String courseName, BatchDetailsDto dto)
			throws IllegalAccessException {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {

				@SuppressWarnings("unchecked")
				List<List<Object>> ListOfItems = (List<List<Object>>) valueWrapper.get();
				int matchingIndex = -1;

				for (int i = 0; i < ListOfItems.size(); i++) {
					List<Object> items = ListOfItems.get(i);
					String getCourseName = String.valueOf(items.get(1).toString());
					if (getCourseName.equalsIgnoreCase(courseName)) {
						matchingIndex = i;
						break;
					}
				}
				List<Object> list = wrapper.extractDtoDetails(dto);
				if (matchingIndex >= 0) {
					ListOfItems.set(matchingIndex, list);

					log.info("Updated cache data for courseNmae: {}", courseName);
				}

			} else {
				log.debug("Data not found in the cache for the specified id: {}", courseName);
			}
		}
	}

}