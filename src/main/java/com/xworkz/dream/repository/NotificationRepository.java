package com.xworkz.dream.repository;

import java.util.List;

public interface NotificationRepository {
	List<List<Object>> notification(String spreadsheetId);

	List<List<Object>> feesNotification(String spreadsheetId);

}
