package com.xworkz.dream.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

//System.out.println("addNewDtoToCache method is running........");
//Cache cache = cacheManager.getCache(cacheName);
//
//if (cache != null) {
//    ValueWrapper valueWrapper = cache.get(key);
//
//    if (valueWrapper != null && valueWrapper.get() instanceof Map) {
//        Map<String, LinkedList<ClientDto>> cacheMap = (Map<String, LinkedList<ClientDto>>) valueWrapper.get();
//
//        if (cacheMap.containsKey(key)) {
//            LinkedList<ClientDto> clientDtoList = cacheMap.get(key);
//            clientDtoList.add(newClientDto);
//            cache.put(key, cacheMap); // Update the cache with the modified map
//            System.out.println("Updated cache: " + cacheMap.toString());
//        } else {
//            // If the key is not present, create a new entry in the cache
//            LinkedList<ClientDto> newClientDtoList = new LinkedList<>();
//            newClientDtoList.add(newClientDto);
//            cacheMap.put(key, newClientDtoList);
//            cache.put(key, cacheMap);
//            System.out.println("Added new entry to cache: " + cacheMap.toString());
//        }
//    } else {
//        // If the key is not present in the cache, create a new entry in the cache
//        LinkedList<ClientDto> newClientDtoList = new LinkedList<>();
//        newClientDto.setId(1);
//        newClientDtoList.add(newClientDto);
//        Map<String, LinkedList<ClientDto>> newCacheMap = new HashMap<>();
//        newCacheMap.put(key, newClientDtoList);
//        cache.put(key, newCacheMap);
//        System.out.println("Added new entry to cache: " + newCacheMap.toString());
//    }
//}
//}

//if (cacheMap.containsKey(key)) {
//	LinkedList<ClientDto> clientList = cacheMap.get(key);
//	log.info("==========================================");
//	int size = clientList.size();
//	newClientDto.setId(size + 1);
//	System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//	clientList.add(newClientDto);
//	cacheMap.put(cacheName, clientList);
//	System.out.println("#############################");
//} else {
//	LinkedList<ClientDto> clientList = new LinkedList<>();
//	newClientDto.setId(1);
//	clientList.add(newClientDto);
//	cacheMap.put(cacheName, clientList);
//}
//
////	cache.put(key, cacheMap);