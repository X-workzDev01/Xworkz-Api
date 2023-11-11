package com.xworkz.dream.scheduler;

import com.xworkz.dream.dto.SheetNotificationDto;

public interface NotificationScheduler {

	public void notification();

	public SheetNotificationDto setNotification(String email);

}
