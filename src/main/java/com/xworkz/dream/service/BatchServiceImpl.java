package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.DreamRepository;
import com.xworkz.dream.wrapper.DreamWrapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BatchServiceImpl implements BatchService {

	@Autowired
	private DreamRepository repo;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private DreamService service;

	public List<TraineeDto> getTraineeDetailsByCourse(String spreadsheetId, String courseName) throws IOException {
		List<TraineeDto> traineeDetails = new ArrayList<>();
		log.debug("reading trainee data by course {} ", courseName);
		return readTraineeDataByCourseName(courseName, spreadsheetId, traineeDetails);

	}

	private List<TraineeDto> readTraineeDataByCourseName(String courseName, String spreadsheetId,
			List<TraineeDto> traineeDetails) throws IOException {
		List<List<Object>> data = repo.readData(spreadsheetId);
		if (courseName != null && data != null) {
			log.debug("course name : {}", courseName);
			traineeDetails = data.stream().filter(row -> row != null && row.size() > 9 && row.contains(courseName))
					.sorted(Comparator.comparing(
							list -> list != null && !list.isEmpty() && list.size() > 24 ? list.get(24).toString() : "",
							Comparator.reverseOrder()))
					.map(wrapper::listToDto).filter(Objects::nonNull).collect(Collectors.toList());
		}
		if (!traineeDetails.isEmpty()) {
			log.debug("");
			return traineeDetails;
		} else {
			log.info("No trainee details found for course: {}", courseName);
			return Collections.emptyList();
		}
	}

	public FollowUpDataDto getTraineeDetailsByCourseInFollowUp(String spreadsheetId, String courseName,
			int startingIndex, int maxIndex) throws IOException {
		FollowUpDataDto followUpDataDto = new FollowUpDataDto(Collections.emptyList(), 0);
		try {
			List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
			List<List<Object>> traineeData = repo.readData(spreadsheetId);
			log.debug("null check for all the data {}", followUpDataDto);
			if (Stream.of(followUpData, traineeData, spreadsheetId, courseName, repo, wrapper, service)
					.anyMatch(Objects::isNull)) {
				return followUpDataDto;
			}
			return getDataByCourseName(spreadsheetId, courseName, traineeData, startingIndex, maxIndex);
		} catch (IOException e) {
			log.error("An IOException occurred: " + e.getMessage(), e);
			return followUpDataDto;
		}
	}

	// for pagination
	public List<FollowUpDto> getLimitedRows(List<List<Object>> values, int startingIndex, int maxRows) {
		List<FollowUpDto> dto = new ArrayList<>();

		if (values != null) {
			int endIndex = Math.min(startingIndex + maxRows, values.size());

			dto = values.subList(startingIndex, endIndex).stream().filter(row -> row != null && !row.isEmpty())
					.map(wrapper::listToFollowUpDTO).collect(Collectors.toList());
		}
		log.debug("Returning values with pagination {}", dto);
		return dto;
	}

	private FollowUpDto assignValuesToFollowUp(TraineeDto dto, FollowUpDto followUp) {
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
		log.debug("assigned values {}", fdto);
		return fdto;
	}

	private FollowUpDataDto getDataByCourseName(String spreadsheetId, String courseName, List<List<Object>> traineeData,
	        int startingIndex, int maxRows) {
	    List<FollowUpDto> followUpDto = traineeData.stream()
	            .filter(row -> row != null && row.size() > 9 && row.contains(courseName))
	            .map(row -> {
	                TraineeDto dto = wrapper.listToDto(row);
	                if (dto == null) {
	                    return null;
	                }
	                FollowUpDto followUp = null;
	                try {
	                	String email = dto.getBasicInfo().getEmail();
	                    log.debug("Attempting to get FollowUp details for email: {}", email);
	                    followUp = service.getFollowUpDetailsByEmail(spreadsheetId, email);
	                    System.out.println(followUp);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                if (followUp == null) {
	                    return null;
	                }

	                FollowUpDto fdto = assignValuesToFollowUp(dto, followUp);
	                return fdto;
	            })
	            .filter(Objects::nonNull)
	            .sorted(Comparator.comparing(FollowUpDto::getRegistrationDate))
	            .collect(Collectors.toList());

	    List<FollowUpDto> limitedRows = getPaginationData(followUpDto, startingIndex, maxRows);

	    // Add logging statements for debugging
	    log.debug("Original followUpDto: {}", followUpDto);

	    FollowUpDataDto dto = new FollowUpDataDto(limitedRows, limitedRows.size());
	    return dto;
	}


	public List<FollowUpDto> getPaginationData(List<FollowUpDto> values, int startingIndex, int maxRows) {
		List<FollowUpDto> dto = new ArrayList<>();

		if (values != null) {
			int endIndex = Math.min(startingIndex + maxRows, values.size());

			dto = values.subList(startingIndex, endIndex).stream()
					.sorted(Comparator.comparing(FollowUpDto::getRegistrationDate)).collect(Collectors.toList());
		}
		log.debug("Returning values with pagination {}", dto);
		return dto;
	}

}
