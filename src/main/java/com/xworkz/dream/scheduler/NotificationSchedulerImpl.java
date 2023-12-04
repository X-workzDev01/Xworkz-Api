package com.xworkz.dream.scheduler;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.utils.Team;
import com.xworkz.dream.service.NotificationService;
import com.xworkz.dream.userYml.TeamList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class NotificationSchedulerImpl implements NotificationScheduler {
	@Autowired
	private NotificationService service;
	private String email;
	private Logger log = LoggerFactory.getLogger(NotificationSchedulerImpl.class);
	private SheetNotificationDto sheetDto;
	@Autowired
	private TeamList team;

	@Override
	public SheetNotificationDto setNotification(String email) {
		log.info("running set Notification ");
		this.email = email;
		notification();
		return sheetDto;
	}

	@Scheduled(fixedRate = 30 * 60 * 1000) // 1000 milliseconds = 1 seconds
	public void notification() {
		log.info("Notification Schudulur is Running");

		try { 
 
			List<Team> teamList = team.getTeam();
			log.debug("Team list is {} ", teamList);
			if (teamList != null) {
				sheetDto = service.notification(teamList, email);
			}

		} catch (IOException e) {
			throw new RuntimeException("Exception occurred: " + e.getMessage(), e);
		}

	}

}
