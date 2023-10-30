package com.xworkz.dream.service;

import java.util.List;

import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;

public interface CacheService {
	void updateCache(String cacheName, String key, List<Object> data);

	void getCacheDataByEmail(String cacheName, String key, String email, TraineeDto dto) throws IllegalAccessException;
	
	void updateCacheFollowUp(String cacheName, String key, String email, FollowUpDto followUpDto) throws IllegalAccessException;

	void updateFollowUpStatus(String cacheName, String spreadsheetId, StatusDto statusDto);
	public void updateFollowUpStatusInCache(String cacheName, String key, List<Object> data);
}
