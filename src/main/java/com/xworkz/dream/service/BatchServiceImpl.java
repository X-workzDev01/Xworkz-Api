package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.DreamRepository;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class BatchServiceImpl implements BatchService {

	@Autowired
	private DreamRepository repo;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private DreamService service;
	private static final Logger logger = LoggerFactory.getLogger(BatchServiceImpl.class);

	public List<TraineeDto> getTraineeDetailsByCourse(String spreadsheetId, String courseName) throws IOException {
		List<List<Object>> data = repo.readData(spreadsheetId);
		List<TraineeDto> traineeDetails = new ArrayList<>();

		if (courseName != null) {
			if (data != null) {
				List<List<Object>> sortedData = data.stream().sorted(Comparator.comparing(
						list -> list != null && !list.isEmpty() && list.size() > 24 ? list.get(24).toString() : "",
						Comparator.reverseOrder())).collect(Collectors.toList());

				traineeDetails = sortedData.stream()
						.filter(row -> row != null && row.size() > 9 && row.contains(courseName))
						.map(wrapper::listToDto).filter(Objects::nonNull) // Filter out any null TraineeDto
						.collect(Collectors.toList());
			}

			if (!traineeDetails.isEmpty()) {
				return traineeDetails;
			} else {
				logger.error("No matching trainee details found for the course: " + courseName);
				return Collections.emptyList();
			}
		} else {
			logger.error("Bad request");
			return Collections.emptyList();
		}
	}

	public List<FollowUpDto> getTraineeDetailsByCourseInFollowUp(String spreadsheetId, String courseName)
			throws IOException {
		try {
			List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> traineeData = repo.readData(spreadsheetId);
			if (followUpData == null || traineeData == null || spreadsheetId == null || courseName == null
					|| repo == null || wrapper == null || service == null) {
				return Collections.emptyList();
			}
			List<FollowUpDto> followUpDto = traineeData.stream()
					.filter(row -> row != null && row.size() > 9 && row.contains(courseName)).map(row -> {
						TraineeDto dto = wrapper.listToDto(row);
						if (dto == null) {
							return null;
						}
						FollowUpDto followUp = null;
						try {
							followUp = service.getFollowUpDetailsByEmail(spreadsheetId, dto.getBasicInfo().getEmail());
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (followUp == null) {
							return null;
						}

						FollowUpDto fdto = new FollowUpDto();
						fdto.setId(dto.getId());
						fdto.setBasicInfo(dto.getBasicInfo());
						if (dto.getCourseInfo() != null) {
							fdto.setCourseName(dto.getCourseInfo().getCourse());
						}
						fdto.setCallback(followUp.getCallback());
						fdto.setCurrentlyFollowedBy(followUp.getCurrentlyFollowedBy());
						fdto.setCurrentStatus(followUp.getCurrentStatus());
						fdto.setJoiningDate(followUp.getJoiningDate());
						fdto.setRegistrationDate(followUp.getRegistrationDate());
						return fdto;
					}).filter(Objects::nonNull).sorted(Comparator.comparing(FollowUpDto::getRegistrationDate))
					.collect(Collectors.toList());

			return followUpDto;
		} catch (IOException e) {
			logger.error("An IOException occurred: " + e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	public List<FollowUpDto> traineeDetailsByCourseAndStatusInFollowUp(String spreadsheetId, String courseName,
			String status) throws IOException {
		try {
			List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> traineeData = repo.readData(spreadsheetId);
			if (followUpData == null || traineeData == null || spreadsheetId == null || courseName == null
					|| repo == null || wrapper == null || service == null || status == null) {
				return Collections.emptyList();
			}
			List<FollowUpDto> followUpDto = traineeData.stream()
					.filter(row -> row != null && row.size() > 9 && row.contains(courseName)).map(row -> {
						TraineeDto dto = wrapper.listToDto(row);
						if (dto == null) {
							return null;
						}
						FollowUpDto followUp = null;
						try {
							followUp = service.getFollowUpDetailsByEmail(spreadsheetId, dto.getBasicInfo().getEmail());
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (followUp == null) {
							return null;
						}

						if (followUp.getCurrentStatus() != null
								&& followUp.getCurrentStatus().equalsIgnoreCase(status)) {
							FollowUpDto fdto = new FollowUpDto();
							fdto.setId(dto.getId());
							fdto.setBasicInfo(dto.getBasicInfo());
							if (dto.getCourseInfo() != null) {
								fdto.setCourseName(dto.getCourseInfo().getCourse());
							}
							fdto.setCallback(followUp.getCallback());
							fdto.setCurrentlyFollowedBy(followUp.getCurrentlyFollowedBy());
							fdto.setCurrentStatus(followUp.getCurrentStatus());
							fdto.setJoiningDate(followUp.getJoiningDate());
							fdto.setRegistrationDate(followUp.getRegistrationDate());

							return fdto;
						} else {
							return null;
						}
					}).filter(Objects::nonNull)
					.sorted(Comparator.comparing(FollowUpDto::getRegistrationDate).reversed())
					.collect(Collectors.toList());
			return followUpDto;

		} catch (IOException e) {
			logger.error("An IOException occurred: " + e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	@Override
	public List<FollowUpDto> getGroupStatus(String spreadsheetId, String status) throws IOException {
		System.out.println("this is getGroupStatus");
		List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
		List<List<Object>> traineeData = repo.readData(spreadsheetId);
		Set<String> interestStatus = new HashSet<>(Arrays.asList("Interested", "RNR", "Not reachable", "Not available",
				"CallDrop", "Incoming", "Not available"));
		Set<String> rnrStatus = new HashSet<>(Arrays.asList("Busy", "RNR", "Not reachable", "Not available", "CallDrop",
				"Incoming", "Not available"));
		Set<String> notInterested = new HashSet<>(Arrays.asList("Drop after course", "drop after placement",
				"higher studies", "joined other institute", "not joining", " wrong number"));

		if (spreadsheetId == null || repo == null || wrapper == null || service == null || status == null) {
			return null;
		}
		if (followUpData == null || traineeData == null || interestStatus == null || rnrStatus == null
				|| notInterested == null) {
			return Collections.emptyList();
		}
		if (status.equalsIgnoreCase("Interested")) {
			System.out.println("status is I");
		}
		return null;
	}

}
