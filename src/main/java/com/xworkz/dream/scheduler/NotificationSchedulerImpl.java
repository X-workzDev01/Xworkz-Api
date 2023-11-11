package com.xworkz.dream.scheduler;

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
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.utils.StatusList;
import com.xworkz.dream.dto.utils.Team;
import com.xworkz.dream.repository.NotificationRepository;
import com.xworkz.dream.userYml.TeamList;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class NotificationSchedulerImpl implements NotificationScheduler {
	@Value("${login.sheetId}")
	private String spreadsheetId;
	private StatusList list;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private DreamUtil util;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private TeamList team;
	private Logger log = LoggerFactory.getLogger(NotificationSchedulerImpl.class);
	private String email;

	@Override
	public SheetNotificationDto setNotification(String email) {
		log.info("running set Notification ");
		this.email = email;
		notification();
		return null;
	}

	@Scheduled(fixedRate = 30 * 60 * 1000) // 1000 milliseconds = 1 seconds
	public void notification() {
		log.info("Notification Schudulur is Running");

		try {

			List<Team> teamList = team.getTeam();
			log.debug("Team list is {} ", teamList);
			if (teamList != null) {
				notification(teamList);
			}

		} catch (IOException e) {
			throw new RuntimeException("Exception occurred: " + e.getMessage(), e);
		}

	}

	public SheetNotificationDto notification(List<Team> teamList) throws IOException {
		StatusList lists = new StatusList();
		log.trace("status list is {} ", lists);
		List<String> statusCheck = lists.getStatusCheck();
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
						log.info("Now checking email {}", email);
						SheetNotificationDto dtos = getNotificationByLoginEmail(statusCheck, today, yesterday,
								afterFoureDay, listOfData);
						log.info("Now checking dto {}", dtos);

						return dtos;
					}

					listOfData.stream().forEach(e -> {
						StatusDto dto = wrapper.listToStatusDto(e);
						endOfTheDayNotification(statusCheck, time, notificationStatus, dto);

					});
				}
			}
			if (LocalTime.now().isAfter(time) && LocalTime.now().isBefore(time.plusMinutes(26))) {

				if (!notificationStatus.isEmpty()) {

					util.sendNotificationToEmail(teamList, notificationStatus);

				}

			}

		}
		return new SheetNotificationDto();

	}

	private void endOfTheDayNotification(List<String> statusCheck, LocalTime time, List<StatusDto> notificationStatus,
			StatusDto dto) {
		if (dto.getCallBack() != null && dto.getCallBack().toString() != "NA") {
			if (LocalDateTime.now().isAfter(LocalDateTime.of((LocalDate.parse(dto.getCallBack())), time))
					&& LocalDateTime.now()
							.isBefore(LocalDateTime.of((LocalDate.parse(dto.getCallBack())), time.plusMinutes(26)))) {

				if (statusCheck.contains(dto.getAttemptStatus())
						&& LocalDate.now().isEqual(LocalDate.parse(dto.getCallBack()))) {

					notificationStatus.add(dto);

				}

			}
		}
	}

	private SheetNotificationDto getNotificationByLoginEmail(List<String> statusCheck, List<StatusDto> today,
			List<StatusDto> yesterday, List<StatusDto> afterFoureDay, List<List<Object>> listOfData) {
		listOfData.stream().forEach(e -> {
			StatusDto dto = wrapper.listToStatusDto(e);

			EverydayNotificationByStatus(statusCheck, today, yesterday, afterFoureDay, dto);

		});
		SheetNotificationDto dto = new SheetNotificationDto(yesterday, today, afterFoureDay);
		return dto;
	}

	private void EverydayNotificationByStatus(List<String> statusCheck, List<StatusDto> today,
			List<StatusDto> yesterday, List<StatusDto> afterFoureDay, StatusDto dto) {
		if (LocalDate.now().isEqual(LocalDate.parse(dto.getCallBack())) && email.equalsIgnoreCase(dto.getAttemptedBy())
				&& statusCheck.contains(dto.getAttemptStatus())) {
			today.add(dto);
		}
		if (LocalDate.now().minusDays(1).isEqual(LocalDate.parse(dto.getCallBack()))
				&& email.equalsIgnoreCase(dto.getAttemptedBy()) && statusCheck.contains(dto.getAttemptStatus())) {
			yesterday.add(dto);

		}
		if (LocalDate.now().plusDays(4).isEqual(LocalDate.parse(dto.getCallBack()))
				&& email.equalsIgnoreCase(dto.getAttemptedBy()) && statusCheck.contains(dto.getAttemptStatus())) {
			afterFoureDay.add(dto);

		}
	}
}
