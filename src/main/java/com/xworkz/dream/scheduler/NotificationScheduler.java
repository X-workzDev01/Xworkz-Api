package com.xworkz.dream.scheduler;

import com.xworkz.dream.dto.FeesNotificationDto;
import com.xworkz.dream.dto.SheetNotificationDto;

public interface NotificationScheduler {

	void notification();

	SheetNotificationDto setNotification(String email);

	FeesNotificationDto feesNotification(String email);

}
