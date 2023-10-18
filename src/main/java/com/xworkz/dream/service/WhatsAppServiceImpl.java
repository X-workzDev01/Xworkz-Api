package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.OthersDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.DreamRepository;
import com.xworkz.dream.repository.WhatsAppRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

	@Autowired
	private WhatsAppRepository repository;
	@Autowired
	private DreamRepository repo;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private DreamUtil util;
	@Autowired
	private DreamService service;
	@Value("${sheets.batchDetailsSheetName}")
	private String batchDetailsSheetName;
	@Value("${sheets.batchDetailsStartRange}")
	private String batchDetailsStartRange;
	@Value("${sheets.batchDetailsEndRange}")
	private String batchDetailsEndRange;

	private static final Logger logger = LoggerFactory.getLogger(WhatsAppServiceImpl.class);

	private int findByCourseNameForUpdate(String spreadsheetId, String courseName) throws IOException {
		ValueRange data = repository.getCourseNameList(spreadsheetId);
		List<List<Object>> values = data.getValues();
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				List<Object> row = values.get(i);
				if (row.size() > 0 && row.get(0).toString().equalsIgnoreCase(courseName)) {
					return i + 2;
				}
			}
		}
		return -1;
	}

	@Override
	public BatchDetails getBatchDetailsListByCourseName(String spreadsheetId, String courseName) throws IOException {
		BatchDetails batch = new BatchDetails();
		if (courseName != null && !courseName.isEmpty()) {
			List<List<Object>> detailsByCourseName = repo.getCourseDetails(spreadsheetId);
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
	public boolean updateWhatsAppLinkByCourseName(String spreadsheetId, String cousreName, String whatsAppLink)
			throws IOException, IllegalAccessException {
		BatchDetails batchDetails = getBatchDetailsListByCourseName(spreadsheetId, cousreName);
		logger.info("loading batch details using sheetId : {}, course name :{} : batchdetails : {}", spreadsheetId,
				cousreName, batchDetails);
		int rowIndex = findByCourseNameForUpdate(spreadsheetId, cousreName);
		String range = batchDetailsSheetName + batchDetailsStartRange + rowIndex + ":" + batchDetailsEndRange
				+ rowIndex;
		batchDetails.setWhatsAppLink(whatsAppLink);
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(batchDetails));
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		UpdateValuesResponse updateBatchDetails = repository.updateBatchDetails(spreadsheetId, range, valueRange);
		if (updateBatchDetails.isEmpty()) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public List<String> getEmailByCourseName(String spreadsheetId, String courseName) throws IOException {
		List<List<Object>> readData = repo.readData(spreadsheetId);
		List<String> emailList = readData.stream().filter(row -> row.size() > 2 && row.get(9) instanceof String).filter(
				row -> courseName.equalsIgnoreCase((String) row.get(9)) && "No".equalsIgnoreCase((String) row.get(23)))
				.map(row -> (String) row.get(2)).collect(Collectors.toList());

		return emailList;
	}

	public synchronized void processAndBulkUpdate(String spreadsheetId, String courseName) throws IOException {
		List<List<Object>> readData = repo.readData(spreadsheetId);
		List<String> emailsToUpdate = readData.stream().filter(row -> row.size() > 2 && row.get(9) instanceof String)
				.filter(row -> courseName.equalsIgnoreCase((String) row.get(9))
						&& "No".equalsIgnoreCase((String) row.get(23)))
				.map(row -> (String) row.get(2)).collect(Collectors.toList());

		if (!emailsToUpdate.isEmpty()) {
			bulkUpdateWhatsAppLink(spreadsheetId, emailsToUpdate, courseName);
		}
	}

	private void bulkUpdateWhatsAppLink(String spreadsheetId, List<String> emails, String courseName)
			throws IOException {
		List<List<Object>> readData = repo.readData(spreadsheetId);

		for (String email : emails) {
			TraineeDto trainee = readData.stream().filter(list -> list.contains(email)).findFirst()
					.map(wrapper::listToDto).orElse(null);

			trainee.getOthersDto().setSendWhatsAppLink("Yes");
			ResponseEntity<String> update = service.update(spreadsheetId, email, trainee);
			logger.info("Bulk update WhatsAppLinkSend: {}", update);
		}
	}

	@Override
	public boolean sendWhatsAppLink(String spreadsheetId, String courseName) throws IOException {

		List<String> emailByCourseName = this.getEmailByCourseName(spreadsheetId, courseName);
		String subject = "WhatsApp Link";
		ResponseEntity<BatchDetails> batchDetailsByCourseName = service.getBatchDetailsByCourseName(spreadsheetId,
				courseName);
		if (!emailByCourseName.isEmpty()) {

			boolean sendWhatsAppLink = util.sendWhatsAppLink(emailByCourseName, subject,
					batchDetailsByCourseName.getBody().getWhatsAppLink());
			if (sendWhatsAppLink == true) {
				this.processAndBulkUpdate(spreadsheetId, courseName);
				return true;
			}

		}

		return false;
	}

	@Override
	public ResponseEntity<List<TraineeDto>> getTraineeDetailsByCourse(String spreadsheetId, String courseName)
			throws IOException {
		List<List<Object>> data = repo.readData(spreadsheetId);
		List<TraineeDto> traineeDetails = new ArrayList<>(); // Initialize as an empty list
		if (courseName != null) {
			if (data != null) {
				List<List<Object>> sortedData = data.stream().sorted(Comparator.comparing(
						list -> list != null && !list.isEmpty() && list.size() > 24 ? list.get(24).toString() : "",
						Comparator.reverseOrder())).collect(Collectors.toList());

				traineeDetails = sortedData.stream().filter(row -> row.size() > 9 && row.contains(courseName))
						.map(wrapper::listToDto).collect(Collectors.toList());
			}

			if (!traineeDetails.isEmpty()) {
				return ResponseEntity.ok(traineeDetails);
			} else {
				logger.error("No matching trainee details found for the course: " + courseName);
				// Return a custom response when no data is found
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
			}
		} else {
			logger.error("Bad request");
			return ResponseEntity.badRequest().build();
		}
	}

	@Override
	public ResponseEntity<List<FollowUpDto>> getTraineeDetailsByCourseInFollowUp(String spreadsheetId, String courseName) throws IOException {
	    try {
	        if (spreadsheetId == null || courseName == null || repo == null || wrapper == null || service == null) {
	            return ResponseEntity.badRequest().build();
	        }

	        List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
	        List<List<Object>> traineeData = repo.readData(spreadsheetId);

	        if (followUpData == null || traineeData == null) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
	        }

	        List<FollowUpDto> followUpDto = traineeData.stream()
	                .filter(row -> row != null && row.size() > 9 && row.contains(courseName))
	                .map(row -> {
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
	                    fdto.setCourseName(dto.getCourseInfo().getCourse());
	                    fdto.setCallback(followUp.getCallback());
	                    fdto.setCurrentlyFollowedBy(followUp.getCurrentlyFollowedBy());
	                    fdto.setCurrentStatus(followUp.getCurrentStatus());
	                    fdto.setJoiningDate(followUp.getJoiningDate());
	                    fdto.setRegistrationDate(followUp.getRegistrationDate());
	                    return fdto;
	                })
	                .filter(Objects::nonNull)
	                .sorted(Comparator.comparing(FollowUpDto::getRegistrationDate)) // Sort by registration date and time
	                .collect(Collectors.toList());

	        return ResponseEntity.ok(followUpDto);
	    } catch (IOException e) {
	        logger.error("An IOException occurred: " + e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
	    }
	}
	@Override
	public ResponseEntity<List<FollowUpDto>> traineeDetailsByCourseAndStatusInFollowUp(String spreadsheetId,
	        String courseName, String status) throws IOException {
	    try {
	        if (spreadsheetId == null || courseName == null || repo == null || wrapper == null || service == null || status == null) {
	            return ResponseEntity.badRequest().build();
	        }

	        List<List<Object>> followUpData = repo.getFollowUpDetails(spreadsheetId);
	        List<List<Object>> traineeData = repo.readData(spreadsheetId);

	        if (followUpData == null || traineeData == null) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
	        }

	        List<FollowUpDto> followUpDto = traineeData.stream()
	                .filter(row -> row != null && row.size() > 9 && row.contains(courseName))
	                .map(row -> {
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

	                    // Check if the followUp's current status matches the provided status
	                    if (followUp.getCurrentStatus().equalsIgnoreCase(status)) {
	                        FollowUpDto fdto = new FollowUpDto();
	                        fdto.setId(dto.getId());
	                        fdto.setBasicInfo(dto.getBasicInfo());
	                        fdto.setCourseName(dto.getCourseInfo().getCourse());
	                        fdto.setCallback(followUp.getCallback());
	                        fdto.setCurrentlyFollowedBy(followUp.getCurrentlyFollowedBy());
	                        fdto.setCurrentStatus(followUp.getCurrentStatus());
	                        fdto.setJoiningDate(followUp.getJoiningDate());
	                        fdto.setRegistrationDate(followUp.getRegistrationDate());
	                       
	                        return fdto;
	                    } else {
	                        return null; // Skip this entry if status does not match
	                    }
	                })
	                .filter(Objects::nonNull)
	                .sorted(Comparator.comparing(FollowUpDto::getRegistrationDate).reversed()) // Sort by registration date and time
	                .collect(Collectors.toList());
	        return  ResponseEntity.ok(followUpDto);
	               
	    } catch (IOException e) {
	        logger.error("An IOException occurred: " + e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
	    }
	}


}
