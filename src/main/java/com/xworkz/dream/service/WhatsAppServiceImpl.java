package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.BatchDetails;
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
		List<String> emailList = readData.stream().filter(row -> row.size() > 2 && row.get(9) instanceof String)
				.filter(row -> "No".equalsIgnoreCase((String) row.get(23))).map(row -> (String) row.get(2))
				.collect(Collectors.toList());
		System.out.println("emailList : " + emailList);

		return emailList;
	}

	public synchronized void processAndBulkUpdate(String spreadsheetId, String courseName) throws IOException {
		List<List<Object>> readData = repo.readData(spreadsheetId);
		List<String> emailsToUpdate = readData.stream().filter(rowData -> shouldUpdateWhatsAppLink(rowData))
				.map(rowData -> (String) rowData.get(2)) // Assuming email is in the first column (index 0)
				.collect(Collectors.toList());

		if (!emailsToUpdate.isEmpty()) {
			bulkUpdateWhatsAppLink(spreadsheetId, emailsToUpdate, courseName);
		}
	}

	private boolean shouldUpdateWhatsAppLink(List<Object> rowData) {
		return rowData.size() > 2 && "No".equalsIgnoreCase((String) rowData.get(23));
	}

	private void bulkUpdateWhatsAppLink(String spreadsheetId, List<String> emails, String courseName) throws IOException {
		List<List<Object>> readData = repo.readData(spreadsheetId);
		System.out.println("emails : "+emails);
		for (String email : emails) {
			TraineeDto trainee = readData.stream().filter(list -> list.contains(email)).findFirst().map(wrapper::listToDto)
					.orElse(null);
			System.out.println("trainee : "+trainee);
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

}
