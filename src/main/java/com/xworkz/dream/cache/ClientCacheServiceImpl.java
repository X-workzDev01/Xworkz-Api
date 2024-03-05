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
		log.info("cache name: {}, cache key: {},", cacheName, key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				log.info("adding list to the cache {}:", data);
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				data.set(0, size + 1);
				data.remove(1);
				((List<List<Object>>) valueWrapper.get()).add(data);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addHRDetailsToCache(String cacheName, String key, List<Object> data) {
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {},", cacheName, key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				log.info("adding list to the cache {}:", data);
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				data.set(0, size + 1);
				((List<List<Object>>) valueWrapper.get()).add(data);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateClientDetailsInCache(String cacheName, String key, List<List<Object>> list)
			throws IllegalAccessException {
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {}", cacheName, key);

		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<List<Object>> cacheItem = ((List<List<Object>>) valueWrapper.get());
				log.info("List data to be added {}", list);
				List<Object> item = list.get(0);
				item.remove(0);
				log.info("{}", item);
				int matchingIndex = -1;
				for (int i = 0; i < cacheItem.size(); i++) {
					Integer val = Integer.parseInt(cacheItem.get(i).get(0).toString());
					if (val.equals(item.get(0))) {
						matchingIndex = i;
					}
				}
				if (matchingIndex != -1) {
					cacheItem.set(matchingIndex, item);
					cache.put(key, cacheItem);
				}
			}
		}

	}

	@Override
	public void updateHrDetailsInCache(String cacheName, String key, List<List<Object>> list)
			throws IllegalAccessException {
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {}", cacheName, key);

		log.info("{}", list);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				log.info("checking valuewrapper");
				@SuppressWarnings("unchecked")
				List<List<Object>> cacheItem = ((List<List<Object>>) valueWrapper.get());
				List<Object> item = list.get(0);
				log.info("{}", item);
				int matchingIndex = -1;
				for (int i = 0; i < cacheItem.size(); i++) {
					Integer val = Integer.parseInt(cacheItem.get(i).get(0).toString());
					if (val.equals(item.get(0))) {
						matchingIndex = i;
					}
				}
				if (matchingIndex != -1) {
					cacheItem.set(matchingIndex, item);
					cache.put(key, cacheItem);
				}
			}

		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void addHrFollowUpToCache(String cacheName, String key, List<Object> list) {
		Cache cache = cacheManager.getCache(cacheName);
		log.info("cache name: {}, cache key: {},", cacheName, key);
		if (cache != null) {
			ValueWrapper valueWrapper = cache.get(key);
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				log.info("adding list to the cache {}:", list);
				int size = (((List<List<Object>>) valueWrapper.get()).size());
				list.set(0, size + 1);
				((List<List<Object>>) valueWrapper.get()).add(list);
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
			if (valueWrapper != null && valueWrapper.get() instanceof List) {
				List<Object> valueList = new ArrayList<Object>(Arrays.asList(value));
				((List<List<Object>>) valueWrapper.get()).add(valueList);
			}
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
