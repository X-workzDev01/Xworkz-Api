package com.xworkz.dream.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class ClientCacheServiceImpl implements ClientCacheService {

	@Autowired
	private CacheManager cacheManager;

	private static final Logger log = LoggerFactory.getLogger(ClientCacheServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public void addNewDtoToCache(String cacheName, String key, List<Object> data) {
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {}", cacheName, key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<List<Object>> cacheData = ((List<List<Object>>) valueWrapper.get());
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				if (cacheData.get(0).contains("#NUM!")) {
					log.info("adding into Cache:{}" ,data);
					cacheData.remove(0);
					data.set(0, size);
					((List<List<Object>>) valueWrapper.get()).add(data);
				} else {
					log.info("adding into Cache:{}", data);
					data.set(0, size + 1);
					((List<List<Object>>) valueWrapper.get()).add(data);
				}

			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void addHRDetailsToCache(String cacheName, String key, List<Object> data) {
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {}", cacheName, key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<List<Object>> cacheData = ((List<List<Object>>) valueWrapper.get());
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				if (cacheData.get(0).contains("#NUM!")) {
					cacheData.remove(0);
					data.set(0, size);
					((List<List<Object>>) valueWrapper.get()).add(data);
					log.info("adding into Cache:{}",data);
				} else {
					data.set(0, size + 1);
					((List<List<Object>>) valueWrapper.get()).add(data);
					log.info("adding into Cache:{}", data);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateClientDetailsInCache(String cacheName, String key, List<Object> list) {
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {}", cacheName, key);

		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<List<Object>> cacheItem = ((List<List<Object>>) valueWrapper.get());
				log.info("List data to be added {}", list);
				int matchingIndex = -1;
				for (int i = 0; i < cacheItem.size(); i++) {
					Integer val = Integer.parseInt(cacheItem.get(i).get(0).toString());
					if (val.equals(list.get(0))) {
						matchingIndex = i;
					}
				}
				if (matchingIndex != -1) {
					cacheItem.set(matchingIndex, list);
					cache.put(key, cacheItem);
				}
			}
		}

	}

	@Override
	public void updateHrDetailsInCache(String cacheName, String key, List<Object> list) {
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {}", cacheName, key);

		log.info("{}", list);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				log.info("checking valuewrapper");
				@SuppressWarnings("unchecked")
				List<List<Object>> cacheItem = ((List<List<Object>>) valueWrapper.get());
				log.info("{}", list);
				int matchingIndex = -1;
				for (int i = 0; i < cacheItem.size(); i++) {
					Integer val = Integer.parseInt(cacheItem.get(i).get(0).toString());
					if (val.equals(list.get(0))) {
						matchingIndex = i;
					}
				}
				if (matchingIndex != -1) {
					cacheItem.set(matchingIndex, list);
					cache.put(key, cacheItem);
				}
			}

		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void addHrFollowUpToCache(String cacheName, String key, List<Object> data) {

		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {}", cacheName, key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<List<Object>> cacheData = ((List<List<Object>>) valueWrapper.get());
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				if (cacheData.get(0).contains("#NUM!")) {
					cacheData.remove(0);
					data.set(0, size);
					((List<List<Object>>) valueWrapper.get()).add(data);
					log.info("adding into Cache:{}" ,data);
				} else {
					data.set(0, size + 1);
					((List<List<Object>>) valueWrapper.get()).add(data);
					log.info("adding into Cache:{}" ,data);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addToCache(String cacheName, String key, String value) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			log.info("Email added into cache {} ", value);
			ValueWrapper valueWrapper = cache.get(key);
			List<List<Object>> cacheValue;
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				cacheValue = (List<List<Object>>) valueWrapper.get();
			} else {
				cacheValue = new ArrayList<>();
			}
			List<Object> valueList = new ArrayList<>(Arrays.asList((Object) value));
			cacheValue.add(valueList);
			cache.put(key, cacheValue);
			log.info("Email: {} added to cache with key: {}", value, key);
		}
	}

	@Override
	public void updateCache(String cacheName, String key, String oldValue, String newValue) {
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {}", cacheName, key);
		if (cache == null) {
			return;
		}
		ValueWrapper valueWrapper = cache.get(key);
		if (valueWrapper == null || !(valueWrapper.get() instanceof List)) {
			return;
		}
		@SuppressWarnings("unchecked")
		List<List<Object>> cacheItem = (List<List<Object>>) valueWrapper.get();
		List<Object> valueList = new ArrayList<>(Arrays.asList(newValue));
		log.info("{}", valueList);

		for (List<Object> obj : cacheItem) {
			if (obj.contains(oldValue)) {
				log.info("Found in sublist: {}", obj);
				int index = obj.indexOf(oldValue);
				obj.set(index, newValue);
			}
		}
	}

}
