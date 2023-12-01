package com.xworkz.dream.resource;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.scheduler.NotificationScheduler;

import io.swagger.annotations.ApiOperation;

@RequestMapping("/api")
@RestController
public class NotificationController {
	private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
	@Autowired
	private NotificationScheduler notificationService;

	@ApiOperation(" Notification API for pending Follow Up")
	@GetMapping("/notification")
	public ResponseEntity<SheetNotificationDto> getFollowupNotification(@RequestParam String email) throws IOException {
		log.info("Request received for notification with email: {}", email);
		return ResponseEntity.ok(notificationService.setNotification(email));

	}

}
