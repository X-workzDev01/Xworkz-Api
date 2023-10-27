package com.xworkz.dream.service;

import java.util.List;

import com.xworkz.dream.dto.TraineeDto;

public interface CacheService {
	void updateCache(String cacheName, String key, List<Object> data);

	void getCacheDataByEmail(String cacheName, String key, String email, TraineeDto dto);
}
