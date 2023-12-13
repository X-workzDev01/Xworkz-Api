package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.utils.StatusList;
import com.xworkz.dream.dto.utils.Team;
import com.xworkz.dream.repository.NotificationRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class NotificationServiceImpl implements NotificationService {
	@Value("${login.sheetId}")
	private String spreadsheetId;
	@Autowired
	private NotificationRepository notificationRepository;
	private SheetNotificationDto notificationDtos;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private DreamUtil util;

	private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

	public SheetNotificationDto notification(List<Team> teamList, String email) throws IOException {
		log.info("Notification service start for email: {}", email);
		StatusList list = new StatusList();
		List<String> statusCheck = list.getStatusCheck();

		LocalTime time = LocalTime.of(17, 59, 01, 500_000_000);
		List<FollowUpDto> notificationStatus = new ArrayList<FollowUpDto>();
		List<FollowUpDto> today = new ArrayList<FollowUpDto>();
		List<FollowUpDto> yesterday = new ArrayList<FollowUpDto>();
		List<FollowUpDto> afterFoureDay = new ArrayList<FollowUpDto>();
		if (spreadsheetId != null) {
			List<List<Object>> listOfData = notificationRepository
					.notification(spreadsheetId);
			if (listOfData != null) {

				if (!listOfData.isEmpty()) {
					if (email != null) {
						System.out.println(listOfData);
						listOfData.stream().forEach(e -> {
							FollowUpDto dto = wrapper.listToFollowUpDTO(e);
							if (LocalDate.now().isEqual(LocalDate.parse(dto.getCallback()))
									&& statusCheck.contains(dto.getCurrentStatus())) {

								today.add(dto);
							}
							if (LocalDate.now().minusDays(1).isEqual(LocalDate.parse(dto.getCallback()))
									&& statusCheck.contains(dto.getCurrentStatus())) {
								yesterday.add(dto);
 
							}
							if (LocalDate.now().plusDays(4).isEqual(LocalDate.parse(dto.getCallback()))

									&& statusCheck.contains(dto.getCurrentStatus())) {
								afterFoureDay.add(dto);

							}

						});
						log.info(
								"After Checking All notification condition result {}---------------------------------- {} ================{}",
								yesterday, today, afterFoureDay);
						SheetNotificationDto dto = new SheetNotificationDto(yesterday, today, afterFoureDay);

						return dto;

					}

					listOfData.stream().forEach(e -> {
						FollowUpDto dto = wrapper.listToFollowUpDTO(e);
						if (dto.getCallback() != null && dto.getCallback().toString() != "NA") {
							if (LocalDateTime.now()
									.isAfter(LocalDateTime.of((LocalDate.parse(dto.getCallback())), time))
									&& LocalDateTime.now().isBefore(LocalDateTime
											.of((LocalDate.parse(dto.getCallback())), time.plusMinutes(26)))) {

								if (statusCheck.contains(dto.getCurrentStatus())
										&& LocalDate.now().isEqual(LocalDate.parse(dto.getCallback()))) {

									notificationStatus.add(dto);

								}

							}
						}

					});
				}
			}
			if (LocalTime.now().isAfter(time) && LocalTime.now().isBefore(time.plusMinutes(26))) {

				if (!notificationStatus.isEmpty()) {

					util.sendNotificationToEmail(teamList, notificationStatus);

				}

			}
 
		}
		return notificationDtos;

	}

}
