package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.BatchRepository;
import com.xworkz.dream.repository.DreamRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.wrapper.DreamWrapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BatchServiceImpl implements BatchService {

	@Autowired
	private RegisterRepository repo;
	@Autowired
	private BatchRepository repository;
	@Autowired
	private DreamWrapper wrapper;
	private BatchDetails batch;
	
	

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
			return ResponseEntity.ok("Batch details added successfully");
		} else {
			return ResponseEntity.ok("Batch details Not added");
		}
	}

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

	
	
	@Override
	public ResponseEntity<BatchDetails> getBatchDetailsByCourseName(String spreadsheetId, String courseName)
			throws IOException {
		List<List<Object>> detailsByCourseName = repository.getCourseDetails(spreadsheetId);
		batch = null;

		List<List<Object>> filter = detailsByCourseName.stream()
				.filter(e -> e.contains(courseName) && e.contains("Active")).collect(Collectors.toList());
		filter.stream().forEach(item -> {
			this.batch = wrapper.batchDetailsToDto(item);
		});
		if (batch != null) {
			return ResponseEntity.ok(this.batch);
		}
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
			return ResponseEntity.ok(coursename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}


}
