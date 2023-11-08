package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.FollowUpDataDto;
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

	public FollowUpDataDto getTraineeDetailsByCourseInFollowUp(String spreadsheetId, String courseName,int startingIndex, int maxIndex) throws IOException {
		FollowUpDataDto followUpDataDto = new FollowUpDataDto(Collections.emptyList(), 0);
		try {
			List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> traineeData = repo.readData(spreadsheetId);
			if (followUpData == null || traineeData == null || spreadsheetId == null || courseName == null
					|| repo == null || wrapper == null || service == null) {
				return followUpDataDto;
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
			FollowUpDataDto dto = new FollowUpDataDto(followUpDto, followUpDto.size());
			return dto;
		} catch (IOException e) {
			logger.error("An IOException occurred: " + e.getMessage(), e);
			return followUpDataDto;
		}
	}

	public FollowUpDataDto traineeDetailsByCourseAndStatusInFollowUp(String spreadsheetId, String courseName,
			String status, String date, int startingIndex, int maxRows) throws IOException {
		FollowUpDataDto followUpDataDto = new FollowUpDataDto(Collections.emptyList(), 0);
		try {
			List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> traineeData = repo.readData(spreadsheetId);
			if (followUpData == null || traineeData == null || spreadsheetId == null || courseName == null
					|| repo == null || wrapper == null || service == null || status == null || date != null) {
				return followUpDataDto;
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

						if (followUp.getCurrentStatus() != null && followUp.getCurrentStatus().equalsIgnoreCase(status)
								|| followUp.getCurrentStatus().equalsIgnoreCase(status)
										&& followUp.getCallback().equalsIgnoreCase(date)
								|| followUp.getCurrentStatus().equalsIgnoreCase(status)
										&& followUp.getCallback().equalsIgnoreCase(date)
										&& followUp.getCourseName().equalsIgnoreCase(courseName)) {
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

//			 List<FollowUpDto> getLimitedRows();
			FollowUpDataDto dto = new FollowUpDataDto(followUpDto, followUpDto.size());
			return dto;
		} catch (IOException e) {
			logger.error("An IOException occurred: " + e.getMessage(), e);
			return followUpDataDto;
		}
	}

	public List<FollowUpDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows) {
		List<FollowUpDto> dto = new ArrayList<>();

		if (values != null) {
			int endIndex = startingIndex + maxRows;

			ListIterator<List<Object>> iterator = values.listIterator(startingIndex);

			while (iterator.hasNext() && iterator.nextIndex() < endIndex) {
				List<Object> row = iterator.next();

				if (row != null && !row.isEmpty()) {
					FollowUpDto followUpDto = wrapper.listToFollowUpDTO(row);
					dto.add(followUpDto);
				}
			}
		}
		return dto;
	}

	@Override
	public FollowUpDataDto getTraineeDetailsByCourseInFollowUp(String spreadsheetId, String courseName)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
