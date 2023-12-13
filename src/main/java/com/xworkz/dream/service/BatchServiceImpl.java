package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.BatchRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class BatchServiceImpl implements BatchService {

	@Autowired
	private RegisterRepository repo;
	@Autowired
	private BatchRepository repository;
	@Autowired
	private DreamWrapper wrapper;
	private BatchDetails batch;
	private static final Logger log = LoggerFactory.getLogger(BatchServiceImpl.class);

	@Override
	public ResponseEntity<String> saveDetails(String spreadsheetId, BatchDetailsDto dto, HttpServletRequest request)
			throws IOException, IllegalAccessException {
		List<List<Object>> data = repository.getBatchId(spreadsheetId).getValues();
		int size = data != null ? data.size() : 0;
		dto.setId(size += 1);
		List<Object> list = wrapper.extractDtoDetails(dto);
		boolean save = repository.saveBatchDetails(spreadsheetId, list);
		// adding to cache
//		cacheService.updateCourseCache("batchDetails", spreadsheetId, list);
		if (save == true) {
			log.info("Batch details added successfully");
			return ResponseEntity.ok("Batch details added successfully");
		} else {
			log.info("Batch details not added");
			return ResponseEntity.ok("Batch details Not added");
		}
	}

	public List<TraineeDto> getTraineeDetailsByCourse(String spreadsheetId, String courseName) throws IOException {
		List<TraineeDto> traineeDetails = new ArrayList<>();
		log.debug("Reading trainee data by course: {} ", courseName);
		return readTraineeDataByCourseName(courseName, spreadsheetId, traineeDetails);

	}

	private List<TraineeDto> readTraineeDataByCourseName(String courseName, String spreadsheetId,
			List<TraineeDto> traineeDetails) throws IOException {
		List<List<Object>> data = repo.readData(spreadsheetId);
		if (courseName != null && data != null) {
			log.debug("Course name: {}", courseName);
			traineeDetails = data.stream().filter(row -> row != null && row.size() > 9 && row.contains(courseName))
					.sorted(Comparator.comparing(
							list -> list != null && !list.isEmpty() && list.size() > 24 ? list.get(24).toString() : "",
							Comparator.reverseOrder()))
					.map(wrapper::listToDto).filter(Objects::nonNull).collect(Collectors.toList());
		}
		if (!traineeDetails.isEmpty()) {
			log.debug("Trainee details found");
			return traineeDetails;
		} else {
			log.info("No trainee details found for course: {}", courseName);
			return Collections.emptyList();
		}
	}

	@Override
	public BatchDetails getBatchDetailsByCourseName(String spreadsheetId, String courseName) throws IOException {
		List<List<Object>> detailsByCourseName = repository.getCourseDetails(spreadsheetId);
		batch = null;
		List<List<Object>> filter = detailsByCourseName.stream()
				.filter(e -> e.contains(courseName) && e.contains("Active")).collect(Collectors.toList());
		filter.stream().forEach(item -> {
			this.batch = wrapper.batchDetailsToDto(item);
		});
		if (batch != null) {
			return batch;
		}
		log.info("No batch details found for course: {}", courseName);
		return null;
	}

	@Override
	public BatchDetails getBatchDetailsListByCourseName(String spreadsheetId, String courseName) throws IOException {
		BatchDetails batch = new BatchDetails();
		if (courseName != null && !courseName.isEmpty()) {
			List<List<Object>> detailsByCourseName = repository.getCourseDetails(spreadsheetId);
			List<List<Object>> data = detailsByCourseName.stream()
					.filter(list -> list.stream().anyMatch(value -> value.toString().equalsIgnoreCase(courseName)))
					.collect(Collectors.toList());
			for (List<Object> row : data) {
				batch = wrapper.batchDetailsToDto(row);
			}
			if (batch != null) {
				log.info("Batch details retrieved successfully for course: {}", courseName);
			} else {
				log.info("No batch details found for course: {}", courseName);
			}
			return batch;
		}
		return null;
	}

	@Override
	public ResponseEntity<List<Object>> getCourseNameByStatus(String spreadsheetId, String status) {
		List<List<Object>> courseNameByStatus;
		try {
			courseNameByStatus = repository.getCourseDetails(spreadsheetId);
			List<Object> coursename = new ArrayList<Object>();
			if (courseNameByStatus != null) {
				for (List<Object> row : courseNameByStatus) {
					if (((String) row.get(7)).equalsIgnoreCase(status)) {
						coursename.add(row.get(1));

					}
				}
			}
			if (!coursename.isEmpty()) {
				log.info("Course names retrieved successfully for status: {}", status);
				return ResponseEntity.ok(coursename);
			} else {
				log.info("No course names found for status: {}", status);
				return null;
			}
		} catch (IOException e) {
			log.error("An IOException occurred: {}", e.getMessage(), e);
			return null;
		}
	}

}
