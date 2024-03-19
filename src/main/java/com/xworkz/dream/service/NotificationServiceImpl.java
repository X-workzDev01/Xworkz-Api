package com.xworkz.dream.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.FeesNotificationDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.SheetNotificationDto;
import com.xworkz.dream.dto.utils.FeesStatusList;
import com.xworkz.dream.dto.utils.StatusList;
import com.xworkz.dream.dto.utils.Team;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.repository.NotificationRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;
import com.xworkz.dream.wrapper.FeesDetilesWrapper;

@Service
public class NotificationServiceImpl implements NotificationService {
	@Value("${login.sheetId}")
	private String spreadsheetId;
	@Autowired
	private NotificationRepository notificationRepository;
	private SheetNotificationDto notificationDtos;
	private FeesNotificationDto feesNotificationDto;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private DreamUtil util;
	@Autowired
	private FeesDetilesWrapper feesWrapper;
	private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

	public SheetNotificationDto notification(List<Team> teamList, String email) {

		log.info("Notification service start for email: {}", email);
		StatusList list = new StatusList();
		List<String> statusCheck = list.getStatusCheck();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalTime time = LocalTime.of(17, 59, 01, 500_000_000);
		List<FollowUpDto> notificationStatus = new ArrayList<FollowUpDto>();
		List<FollowUpDto> today = new ArrayList<FollowUpDto>();
		List<FollowUpDto> yesterday = new ArrayList<FollowUpDto>();
		List<FollowUpDto> afterFoureDay = new ArrayList<FollowUpDto>();

		if (spreadsheetId != null) {
			List<List<Object>> listOfData = notificationRepository.notification(spreadsheetId);
			if (listOfData != null) {

				if (!listOfData.isEmpty()) {
					if (email != null) {
						listOfData.stream().forEach(e -> {
							FollowUpDto dto = wrapper.listToFollowUpDTO(e);
							if (statusCheck.contains(dto.getCurrentStatus())) {
								if (dto.getFlag() != null && !dto.getFlag().equalsIgnoreCase("Inactive")) {
									if (dto.getCallback().length() > 11 && LocalDate.now().isEqual(LocalDate
											.parse(LocalDateTime.parse(dto.getCallback()).format(dateFormatter)))) {
										today.add(dto);
									} else if (dto.getCallback().length() <= 10 && LocalDate.now().isEqual(LocalDate
											.parse(LocalDate.parse(dto.getCallback()).format(dateFormatter)))) {
										today.add(dto);
									}

									if (dto.getCallback().length() > 11
											&& LocalDate.now().minusDays(1).isEqual(LocalDate.parse(
													LocalDateTime.parse(dto.getCallback()).format(dateFormatter)))) {
										yesterday.add(dto);
									} else if (dto.getCallback().length() <= 10
											&& LocalDate.now().minusDays(1).isEqual(LocalDate
													.parse(LocalDate.parse(dto.getCallback()).format(dateFormatter)))) {
										yesterday.add(dto);
									}

									if (dto.getCallback().length() > 11 && LocalDate.now().plusDays(4).isEqual(LocalDate
											.parse(LocalDateTime.parse(dto.getCallback()).format(dateFormatter)))) {
										afterFoureDay.add(dto);
									} else if (dto.getCallback().length() <= 10
											&& LocalDate.now().plusDays(4).isEqual(LocalDate
													.parse(LocalDate.parse(dto.getCallback()).format(dateFormatter)))) {
										afterFoureDay.add(dto);

									}
								}
							}
						});
						log.debug(
								"After Checking All notification condition result {}---------------------------------- {} ================{}",
								yesterday, today, afterFoureDay);

						SheetNotificationDto dto = new SheetNotificationDto(yesterday, today, afterFoureDay);

						return dto;
					}

					listOfData.stream().forEach(e -> {
						FollowUpDto dto = wrapper.listToFollowUpDTO(e);

						if (dto.getCallback() != null && dto.getFlag() != null
								&& !dto.getFlag().equalsIgnoreCase("Inactive")
								&& statusCheck.contains(dto.getCurrentStatus())) {
							if (dto.getCallback().length() > 10
									&& LocalDateTime.now()
											.isAfter(LocalDateTime.of(LocalDate.parse(
													LocalDateTime.parse(dto.getCallback()).format(dateFormatter)),
													time))
									&& LocalDateTime.now()
											.isBefore(LocalDateTime.of(LocalDate.parse(
													LocalDateTime.parse(dto.getCallback()).format(dateFormatter)),
													time.plusMinutes(29)))

									|| dto.getCallback().length() == 10
											&& LocalDateTime.now()
													.isAfter(LocalDateTime.of(LocalDate.parse(
															LocalDate.parse(dto.getCallback()).format(dateFormatter)),
															time))
											&& LocalDateTime.now().isBefore(LocalDateTime.of(
													LocalDate.parse(
															LocalDate.parse(dto.getCallback()).format(dateFormatter)),
													time.plusMinutes(26)))) {
								if (dto.getCallback().length() > 11 && LocalDate.now().isEqual(LocalDate
										.parse(LocalDateTime.parse(dto.getCallback()).format(dateFormatter)))) {

									notificationStatus.add(dto);
								} else if (dto.getCallback().length() == 10 && LocalDate.now().isEqual(
										LocalDate.parse(LocalDate.parse(dto.getCallback()).format(dateFormatter)))) {

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

	@Override
	public FeesNotificationDto feesNotification(List<Team> teamList, String email) {

		log.info("Notification service start for email: {}", email);
		FeesStatusList list = new FeesStatusList();
		List<String> statusCheck = list.getStatusList();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalTime time = LocalTime.of(16, 59, 01, 500_000_000);
		List<FeesDto> notificationStatus = new ArrayList<FeesDto>();
		List<FeesDto> today = new ArrayList<FeesDto>();
		List<FeesDto> yesterday = new ArrayList<FeesDto>();
		List<FeesDto> afterFoureDay = new ArrayList<FeesDto>();

		if (spreadsheetId != null) {
			List<List<Object>> listOfData = notificationRepository.feesNotification(spreadsheetId);
			if (listOfData != null) {

				if (!listOfData.isEmpty()) {
					if (email != null) {
						listOfData.stream().forEach(e -> {
							FeesDto dto = feesWrapper.listToFeesDTO(e);
							if (statusCheck.contains(dto.getFeesStatus())) {
								if (dto.getSoftFlag() != null && !dto.getSoftFlag().equalsIgnoreCase("Inactive")) {
									if (dto.getFeesHistoryDto().getFollowupCallbackDate().length() > 11
											&& LocalDate.now()
													.isEqual(LocalDate.parse(LocalDateTime
															.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
															.format(dateFormatter)))) {
										today.add(dto);
									} else if (dto.getFeesHistoryDto().getFollowupCallbackDate().length() <= 10
											&& LocalDate.now()
													.isEqual(LocalDate.parse(LocalDate
															.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
															.format(dateFormatter)))) {
										today.add(dto);
									}

									if (dto.getFeesHistoryDto().getFollowupCallbackDate().length() > 11
											&& LocalDate.now().minusDays(1)
													.isEqual(LocalDate.parse(LocalDateTime
															.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
															.format(dateFormatter)))) {
										yesterday.add(dto);
									} else if (dto.getFeesHistoryDto().getFollowupCallbackDate().length() <= 10
											&& LocalDate.now().minusDays(1)
													.isEqual(LocalDate.parse(LocalDate
															.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
															.format(dateFormatter)))) {
										yesterday.add(dto);
									}

									if (dto.getFeesHistoryDto().getFollowupCallbackDate().length() > 11
											&& LocalDate.now().plusDays(4)
													.isEqual(LocalDate.parse(LocalDateTime
															.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
															.format(dateFormatter)))) {
										afterFoureDay.add(dto);
									} else if (dto.getFeesHistoryDto().getFollowupCallbackDate().length() <= 10
											&& LocalDate.now().plusDays(4)
													.isEqual(LocalDate.parse(LocalDate
															.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
															.format(dateFormatter)))) {
										afterFoureDay.add(dto);

									}
								}
							}

						});
						FeesNotificationDto dto = new FeesNotificationDto(yesterday, today, afterFoureDay);

						return dto;
					}

					listOfData.stream().forEach(e -> {
						FeesDto dto = feesWrapper.listToFeesDTO(e);
						if (dto.getFeesHistoryDto() != null && dto.getFeesHistoryDto().getFollowupCallbackDate() != null
								&& dto.getSoftFlag() != null && !dto.getSoftFlag().equalsIgnoreCase("Inactive")
								&& statusCheck.contains(dto.getFeesStatus())) {
							if (dto.getFeesHistoryDto().getFollowupCallbackDate().length() > 10
									&& LocalDateTime.now()
											.isAfter(LocalDateTime.of(LocalDate.parse(LocalDateTime
													.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
													.format(dateFormatter)), time))
									&& LocalDateTime.now()
											.isBefore(LocalDateTime.of(LocalDate.parse(LocalDateTime
													.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
													.format(dateFormatter)), time.plusMinutes(29)))

									|| dto.getFeesHistoryDto().getFollowupCallbackDate().length() == 10
											&& LocalDateTime.now()
													.isAfter(LocalDateTime.of(LocalDate.parse(LocalDate
															.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
															.format(dateFormatter)), time))
											&& LocalDateTime.now()
													.isBefore(
															LocalDateTime.of(
																	LocalDate.parse(LocalDate
																			.parse(dto.getFeesHistoryDto()
																					.getFollowupCallbackDate())
																			.format(dateFormatter)),
																	time.plusMinutes(26)))) {
								if (dto.getFeesHistoryDto().getFollowupCallbackDate().length() > 11
										&& LocalDate.now()
												.isEqual(LocalDate.parse(LocalDateTime
														.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
														.format(dateFormatter)))) {

									notificationStatus.add(dto);
								} else if (dto.getFeesHistoryDto().getFollowupCallbackDate().length() == 10
										&& LocalDate.now()
												.isEqual(LocalDate.parse(LocalDate
														.parse(dto.getFeesHistoryDto().getFollowupCallbackDate())
														.format(dateFormatter)))) {

									notificationStatus.add(dto);
								}

							}
						}

					});
				}
			}
			if (LocalTime.now().isAfter(time) && LocalTime.now().isBefore(time.plusMinutes(26))) {

				if (!notificationStatus.isEmpty()) {

					util.sendFeesNotificationToEmail(teamList, notificationStatus);

				}

			}

		}
		return feesNotificationDto;
	}

}
