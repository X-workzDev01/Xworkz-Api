package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.utils.Team;

public interface NotificationService {
	 SheetNotificationDto notification(List<Team> teamList, String email) throws IOException;

}
