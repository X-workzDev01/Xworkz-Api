package com.xworkz.dream.dto.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;
import com.xworkz.dream.repository.FeesRepository;

@Service
public class WrapperUtil {
	@Value("${sheets.feesEmailRange}")
	private String feesEmailRange;
	@Autowired
	private FeesRepository feesRepository;
	Logger log = LoggerFactory.getLogger(WrapperUtil.class);

	public List<Object> extractDtoDetails(Object dto) throws IllegalAccessException {
		List<Object> detailsList = new ArrayList<>();

		// Get all fields of the DTO class, including inherited fields
		Class<?> dtoClass = dto.getClass();
		Field[] fields = dtoClass.getDeclaredFields();

		for (Field field : fields) {
			// Make private fields accessible
			field.setAccessible(true);

			// Extract the value of the field from the DTO object
			Object fieldValue = field.get(dto);

			if (fieldValue != null && !field.getType().isPrimitive() && !field.getType().getName().startsWith("java")) {
				// Handle association with another DTO
				List<Object> subDtoDetails = extractDtoDetails(fieldValue);
				detailsList.addAll(subDtoDetails);

			} else {
				// Add the value to the list
				detailsList.add(fieldValue);
			}
		}

		return detailsList;
	}

	public int findIndex(String email) throws IOException {
		ValueRange data = feesRepository.getEmailList(feesEmailRange);
		List<List<Object>> values = data.getValues();
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				List<Object> row = values.get(i);
				if (row.size() > 0 && row.get(0).toString().equalsIgnoreCase(email)) {
					return i + 2;
				}
			}
		}
		return -1;
	}

	public String upateValuesSet(FeesDto feesDto, FeesDto dto) throws IllegalAccessException, IOException {
		if (dto.getFeesHistoryDto().getEmail().equalsIgnoreCase(feesDto.getFeesHistoryDto().getEmail())) {
			setToFeesHistory(feesDto.getFeesHistoryDto().getPaidAmount(), feesDto);
			FeesDto updateDto = new FeesDto();
			log.info("Fees update running");
			updateDto = feesDto;
			updateDto.setFeesHistoryDto(feesDto.getFeesHistoryDto());
			updateDto.getAdmin().setCreatedBy(dto.getAdmin().getCreatedBy());
			updateDto.getAdmin().setCreatedOn(dto.getAdmin().getCreatedOn());
			updateDto.getFeesHistoryDto().setFeesfollowupDate(LocalDate.now().toString());
			updateDto.setBalance(null);
			updateDto.getFeesHistoryDto()
					.setPaidAmount(String.valueOf(Integer.parseInt(feesDto.getFeesHistoryDto().getPaidAmount())
							+ Integer.parseInt(dto.getFeesHistoryDto().getPaidAmount())));
//			System.out.println("gggg           " + Integer.parseInt(feesDto.getFeesHistoryDto().getPaidAmount())
//					+ Integer.parseInt(dto.getFeesHistoryDto().getPaidAmount()));
			updateDto.setFeesStatus("Pending");
			updateDto.getFeesHistoryDto().setId(null);
			updateDto.setCourseName(null);
			updateDto.setMailSendStatus(dto.getMailSendStatus());
			updateDto.setReminderDate(dto.getReminderDate());
			updateDto.setSoftFlag(dto.getSoftFlag());
			updateDto.setTotalAmount(null);

			try {

				int index = findIndex(feesDto.getFeesHistoryDto().getEmail());
				String followupRanges = "FeesDetiles!B" + index + ":T" + index;
				feesRepository.updateFeesDetiles(followupRanges, extractDtoDetails(updateDto));

				log.info("FeesDetiles Updated Sucessfully");
				return "Feesfollowup and feesDetiles Updated Sucessfully";

			} catch (IllegalAccessException | IOException e) {

			}
		}
		return "Email is miss matching data could not be updated ";
	}

	private void setToFeesHistory(String fees, FeesDto dto) throws IllegalAccessException, IOException {
		FeesHistoryDto feesHistoryDto = new FeesHistoryDto();
		feesHistoryDto.setFeesfollowupDate(LocalDate.now().toString());
		feesHistoryDto.setEmail(dto.getFeesHistoryDto().getEmail());
		feesHistoryDto.setLastFeesPaidDate(dto.getFeesHistoryDto().getLastFeesPaidDate());
		feesHistoryDto.setFollowupCallbackDate(dto.getFeesHistoryDto().getFollowupCallbackDate());
		feesHistoryDto.setPaymentMode(dto.getFeesHistoryDto().getPaymentMode());
		feesHistoryDto.setTransectionId(dto.getFeesHistoryDto().getTransectionId());
		feesHistoryDto.setPaidAmount(fees);
		feesHistoryDto.setFollowupstatus(dto.getFeesHistoryDto().getFollowupstatus());
		feesHistoryDto.setId(null);

		feesRepository.updateDetilesToFollowUp("FeesFollowup!B2", extractDtoDetails(feesHistoryDto));
	}

}
