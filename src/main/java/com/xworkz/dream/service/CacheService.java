package com.xworkz.dream.service;

import java.util.List;

import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;

public interface CacheService {
	void updateCache(String cacheName, String key, List<Object> data);

	void EmailUpdate(String cacheName, String key, String oldEmail, String newEmail);

	void getCacheDataByEmail(String cacheName, String key, String email, TraineeDto dto);

	void updateCacheFollowUp(String cacheName, String key, String email, FollowUpDto followUpDto);

	void updateFollowUpStatus(String cacheName, String spreadsheetId, String email, List<Object> statusData);

	void addToFollowUpStatusCache(String cacheName, String key, List<Object> data);

	void updateCourseCache(String cacheName, String key, List<Object> data);

	void addFollowUpToCache(String cacheName, String spreadSheetId, List<Object> data);

	void addEmailToCache(String cacheName, String spreadSheetId, String email);

	void addContactNumberToCache(String cacheName, String spreadSheetId, Long contactNumber);

	void getCacheDataByEmail(String cacheName, String key, String oldEmail, String newEmail);

	void addAttendancdeToCache(String cacheName, String key, List<Object> data);

	void updateCacheAttendancde(String cacheName, String key, Integer id, AttendanceDto dto);

	void updateCacheBatch(String cacheName, String key, String courseNAME, BatchDetailsDto dto);

	void addToCache(String cacheName, String key, String value);

	void updateBirthDayInfoInCache(String cacheName, String key, String email, BirthDayInfoDto birthDayInfoDto);

	void addBirthDayToCache(String cacheName, String key, List<Object> data);
}
