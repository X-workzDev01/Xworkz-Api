package com.xworkz.dream.wrapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.BatchAttendanceDto;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.repository.BatchRepository;
import com.xworkz.dream.service.CacheService;

@Component
public class BatchWrapper {

	@Autowired
	private BatchRepository repository;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private CacheService cacheService;
	@Value("${login.sheetId}")
	private String sheetId;
	@Value("${sheets.batchDetailsSheetName}")
	private String batchDetailsSheetName;
	@Value("${sheets.batchDetailsStartRange}")
	private String batchDetailsStartRange;
	@Value("${sheets.batchDetailsEndRange}")
	private String batchDetailsEndRange;

	private static final Logger log = LoggerFactory.getLogger(BatchWrapper.class);

	public String updateBatchValueSet(BatchDetailsDto updateBatchValue, BatchDetailsDto detailsDto) {
		if (detailsDto.getCourseName().equalsIgnoreCase(updateBatchValue.getCourseName())) {
			BatchDetailsDto dto = new BatchDetailsDto();
			dto = updateBatchValue;
			if (updateBatchValue.getCourseName() != null) {
				dto.setCourseName(updateBatchValue.getCourseName());
			} else {
				dto.setCourseName(detailsDto.getCourseName());
			}
			if (updateBatchValue.getTrainerName() != null) {
				dto.setTrainerName(updateBatchValue.getTrainerName());
			} else {
				dto.setTrainerName(detailsDto.getTrainerName());
			}
			if (updateBatchValue.getStartDate() != null) {
				dto.setStartDate(updateBatchValue.getStartDate());
			} else {
				dto.setStartDate(detailsDto.getStartDate());
			}
			if (updateBatchValue.getBatchType() != null) {
				dto.setBatchType(updateBatchValue.getBatchType());
			} else {
				dto.setBatchType(detailsDto.getBatchType());
			}
			if (updateBatchValue.getStartTime() != null) {
				dto.setStartTime(updateBatchValue.getStartTime());
			} else {
				dto.setStartTime(detailsDto.getStartTime());
			}
			if (updateBatchValue.getBranchName() != null) {
				dto.setBranchName(updateBatchValue.getBranchName());
			} else {
				dto.setBranchName(detailsDto.getBranchName());
			}
			if (updateBatchValue.getBatchStatus() != null) {
				dto.setBatchStatus(updateBatchValue.getBatchStatus());
			} else {
				dto.setBatchStatus(detailsDto.getBatchStatus());
			}
			if (updateBatchValue.getWhatsAppLink() != null) {
				dto.setWhatsAppLink(updateBatchValue.getWhatsAppLink());
			} else {
				dto.setWhatsAppLink(detailsDto.getWhatsAppLink());
			}
			if (updateBatchValue.getTotalAmount() != null) {
				dto.setTotalAmount(updateBatchValue.getTotalAmount());
			} else {
				dto.setTotalAmount(detailsDto.getTotalAmount());
			}
			if (updateBatchValue.getTotalClass() != null) {
				dto.setTotalClass(updateBatchValue.getTotalClass());
			} else {
				dto.setTotalClass(detailsDto.getTotalClass());
			}
			
			try {
				int rowIndex = findIndex(updateBatchValue.getCourseName());
				String range = batchDetailsSheetName + batchDetailsStartRange + rowIndex + ":" + batchDetailsEndRange
						+ rowIndex;

				List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(dto));
				ValueRange valueRange = new ValueRange();
				if (!values.isEmpty()) {
					List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(0, values.get(0).size()));
					modifiedValues.remove(0);
					values.set(0, modifiedValues);
					log.debug("values {}", values);
				}
				valueRange.setValues(values);
				cacheService.updateCacheBatch("batchDetails", "listOfBatch",dto.getCourseName(), dto);
				repository.updateBatchDetails(sheetId, range, valueRange);

			} catch (IllegalAccessException | IOException e) {
				log.error(e.getMessage());
			}
		}
		return "Batch Details updated successfully";
		
	}
	

	private int findIndex(String courseName) throws IOException {
		List<List<Object>> data = repository.getCourseDetails(sheetId);
		if (data != null) {
			if (data != null && !data.isEmpty()) {
				for (int i = 0; i < data.size(); i++) {
					List<Object> row = data.get(i);
					if (row.size() > 0 && row.get(1).toString().equalsIgnoreCase(courseName.toString())) {
						return i + 2;
					}
				}
			}
		}
		return -1;
	}
	
	public BatchAttendanceDto setBatchValues(BatchDetailsDto batchDetailsDto) {
		BatchAttendanceDto dto=new BatchAttendanceDto();
		dto.setBatchName(batchDetailsDto.getCourseName());
		dto.setTrainerName(batchDetailsDto.getTrainerName());
		dto.setPresentDate(LocalDate.now().toString());
		return dto;
		
	}

}
