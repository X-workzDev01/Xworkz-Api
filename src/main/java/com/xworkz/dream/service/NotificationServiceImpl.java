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

import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.StatusDto;
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

	private Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

	public SheetNotificationDto notification(List<Team> teamList, String email) throws IOException {
		log.info("Notificaton service start {} ", email);
		StatusList list = new StatusList();
		List<String> statusCheck = list.getStatusCheck();

		LocalTime time = LocalTime.of(17, 59, 01, 500_000_000);
		List<StatusDto> notificationStatus = new ArrayList<StatusDto>();
		List<StatusDto> today = new ArrayList<StatusDto>();
		List<StatusDto> yesterday = new ArrayList<StatusDto>();
		List<StatusDto> afterFoureDay = new ArrayList<StatusDto>();
		if (spreadsheetId != null) {
			List<List<Object>> listOfData = notificationRepository.notification(spreadsheetId);
			if (listOfData != null) {

				if (!listOfData.isEmpty()) {
					if (email != null) {
						listOfData.stream().forEach(e -> {
							StatusDto dto = wrapper.listToStatusDto(e);
							if (LocalDate.now().isEqual(LocalDate.parse(dto.getCallBack()))
									&& email.equalsIgnoreCase(dto.getAttemptedBy())
									&& statusCheck.contains(dto.getAttemptStatus())) {

								today.add(dto);
							}
							if (LocalDate.now().minusDays(1).isEqual(LocalDate.parse(dto.getCallBack()))
									&& email.equalsIgnoreCase(dto.getAttemptedBy())
									&& statusCheck.contains(dto.getAttemptStatus())) {
								yesterday.add(dto);

							}
							if (LocalDate.now().plusDays(4).isEqual(LocalDate.parse(dto.getCallBack()))
									&& email.equalsIgnoreCase(dto.getAttemptedBy())
									&& statusCheck.contains(dto.getAttemptStatus())) {
								afterFoureDay.add(dto);

							}

						});
						log.debug(
								"After Checking All notification condition result {}---------------------------------- {} ================{}",
								yesterday, today, afterFoureDay);
						SheetNotificationDto dto = new SheetNotificationDto(yesterday, yesterday, afterFoureDay);

						return dto;

					}

					listOfData.stream().forEach(e -> {
						StatusDto dto = wrapper.listToStatusDto(e);
						if (dto.getCallBack() != null && dto.getCallBack().toString() != "NA") {
							if (LocalDateTime.now()
									.isAfter(LocalDateTime.of((LocalDate.parse(dto.getCallBack())), time))
									&& LocalDateTime.now().isBefore(LocalDateTime
											.of((LocalDate.parse(dto.getCallBack())), time.plusMinutes(26)))) {

								if (statusCheck.contains(dto.getAttemptStatus())
										&& LocalDate.now().isEqual(LocalDate.parse(dto.getCallBack()))) {

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
