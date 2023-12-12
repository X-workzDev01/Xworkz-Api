package com.xworkz.dream.service;

import java.io.IOException;
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
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.BatchRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

	@Autowired
	private BatchRepository repository;
	@Autowired
	private RegisterRepository repo;
	@Autowired
	private RegistrationService registrationService;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private DreamUtil util;
	@Autowired
	private BatchService service;
	@Value("${sheets.batchDetailsSheetName}")
	private String batchDetailsSheetName;
	@Value("${sheets.batchDetailsStartRange}")
	private String batchDetailsStartRange;
	@Value("${sheets.batchDetailsEndRange}")
	private String batchDetailsEndRange;

	private static final Logger log = LoggerFactory.getLogger(WhatsAppServiceImpl.class);
	
	private int findByCourseNameForUpdate(String spreadsheetId, String courseName) throws IOException {
		ValueRange data = repository.getCourseNameList(spreadsheetId);
		List<List<Object>> values = data.getValues();
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				List<Object> row = values.get(i);
				if (row.size() > 0 && row.get(0).toString().equalsIgnoreCase(courseName)) {
					   log.info("Found row index {} for course name: {}", i + 2, courseName);
					return i + 2;
				}
			}
		}
		 log.warn("Course name not found: {}", courseName);
		return -1;
	}

	@Override
	public boolean updateWhatsAppLinkByCourseName(String spreadsheetId, String courseName, String whatsAppLink)
			throws IOException, IllegalAccessException {
		BatchDetails batchDetails =service.getBatchDetailsListByCourseName(spreadsheetId, courseName);
		log.info("loading batch details using sheetId : {}, course name :{} : batchdetails : {}", spreadsheetId,
				courseName, batchDetails);
		int rowIndex = findByCourseNameForUpdate(spreadsheetId, courseName);
		String range = batchDetailsSheetName + batchDetailsStartRange + rowIndex + ":" + batchDetailsEndRange
				+ rowIndex;
		batchDetails.setWhatsAppLink(whatsAppLink);
		List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(batchDetails));
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		UpdateValuesResponse updateBatchDetails = repository.updateBatchDetails(spreadsheetId, range, valueRange);
		if (updateBatchDetails.isEmpty()) {
		     log.warn("Update failed for WhatsApp link. Course name: {}", courseName);
             return false;
         } else {
             log.info("WhatsApp link updated successfully. Course name: {}", courseName);
             return true;
         }

	}

	@Override
	public List<String> getEmailByCourseName(String spreadsheetId, String courseName) throws IOException {
		List<List<Object>> readData = repo.readData(spreadsheetId);
		List<String> emailList = readData.stream().filter(row -> row.size() > 2 && row.get(9) instanceof String).filter(
				row -> courseName.equalsIgnoreCase((String) row.get(9)) && "No".equalsIgnoreCase((String) row.get(23)))
				.map(row -> (String) row.get(2)).collect(Collectors.toList());
		  log.info("Found {} emails by course name: {}", emailList.size(), courseName);
		return emailList;
	}

	public synchronized void processAndBulkUpdate(String spreadsheetId, String courseName) throws IOException {
		List<List<Object>> readData = repo.readData(spreadsheetId);
		List<String> emailsToUpdate = readData.stream().filter(row -> row.size() > 2 && row.get(9) instanceof String)
				.filter(row -> courseName.equalsIgnoreCase((String) row.get(9))
						&& "No".equalsIgnoreCase((String) row.get(23)))
				.map(row -> (String) row.get(2)).collect(Collectors.toList());

		if (!emailsToUpdate.isEmpty()) {
			   log.info("Bulk updating WhatsApp link for {} emails", emailsToUpdate.size());
			bulkUpdateWhatsAppLink(spreadsheetId, emailsToUpdate, courseName);
		}
	}

	private void bulkUpdateWhatsAppLink(String spreadsheetId, List<String> emails, String courseName)
			throws IOException {
		   log.info("Processing and bulk updating in spreadsheetId: {} for courseName: {}", spreadsheetId, courseName);
		List<List<Object>> readData = repo.readData(spreadsheetId);

		for (String email : emails) {
			TraineeDto trainee = readData.stream().filter(list -> list.contains(email)).findFirst()
					.map(wrapper::listToDto).orElse(null);
			 if (trainee != null) {
			trainee.getOthersDto().setSendWhatsAppLink("Yes");
			ResponseEntity<String> update = registrationService.update(spreadsheetId, email, trainee);
			  log.info("Bulk update WhatsAppLinkSend for email {}: {}", email, update);
			 } else {
                 log.warn("Trainee not found for email: {}", email);
             }
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
				log.info("WhatsApp link sent successfully for courseName: {}", courseName);
				this.processAndBulkUpdate(spreadsheetId, courseName);
				return true;
			} else {
                log.warn("Failed to send WhatsApp link for courseName: {}", courseName);
            }
        } else {
            log.warn("No emails found for courseName: {}", courseName);
        }
		return false;
	}

	
	
}
