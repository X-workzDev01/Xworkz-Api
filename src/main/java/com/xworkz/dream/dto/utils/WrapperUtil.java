package com.xworkz.dream.dto.utils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.cache.FeesFollowUpCacheService;
import com.xworkz.dream.constants.CacheConstant;
import com.xworkz.dream.constants.FeesConstant;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesFinalDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;
import com.xworkz.dream.repository.FeesRepository;

@Service
public class WrapperUtil {
	@Autowired
	private FeesFinalDto feesFinalDtoRanges;
	@Autowired
	private FeesRepository feesRepository;
	@Autowired
	private FeesFollowUpCacheService feesCacheService;
	Logger log = LoggerFactory.getLogger(WrapperUtil.class);

	public List<Object> extractDtoDetails(Object dto) {
		List<Object> detailsList = new ArrayList<>();
		Class<?> dtoClass = dto.getClass();
		Field[] fields = dtoClass.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			Object fieldValue;
			try {
				fieldValue = field.get(dto);

				if (fieldValue != null && !field.getType().isPrimitive()
						&& !field.getType().getName().startsWith("java")) {
					List<Object> subDtoDetails = extractDtoDetails(fieldValue);
					detailsList.addAll(subDtoDetails);
				} else {
					detailsList.add(fieldValue);
				}
			} catch (Exception e) {
				log.error("Error converting data {} ", e);
			}
		}

		return detailsList;
	}

	public int findIndex(String email) {
		List<List<Object>> values = feesRepository.getEmailList(feesFinalDtoRanges.getFeesEmailRange());
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

	public String upateValuesSet(FeesDto feesDto, FeesDto dto) {
		if (dto.getFeesHistoryDto().getEmail().equalsIgnoreCase(feesDto.getFeesHistoryDto().getEmail())) {
			setToFeesHistory(feesDto.getFeesHistoryDto().getPaidAmount(), feesDto);
			FeesDto updateDto = new FeesDto();
			updateDto = feesDto;
			updateDto.setFeesHistoryDto(feesDto.getFeesHistoryDto());
			updateDto.getAdmin().setCreatedBy(dto.getAdmin().getCreatedBy());
			updateDto.getAdmin().setCreatedOn(dto.getAdmin().getCreatedOn());
			updateDto.getAdmin().setUpdatedOn(LocalDate.now().toString());
			if (feesDto.getComments() != null) {
				updateDto.setComments(feesDto.getComments());
			} else {
				updateDto.setComments(dto.getComments());
			}
			if (feesDto.getLateFees() != null) {
				updateDto.setLateFees(feesDto.getLateFees());
			} else {
				updateDto.setLateFees(dto.getLateFees());
			}
			updateDto.setId(dto.getId());
			updateDto.getFeesHistoryDto().setFeesfollowupDate(LocalDate.now().toString());
			updateDto.setBalance(null);
			updateDto.getFeesHistoryDto()
					.setPaidAmount(String.valueOf(Integer.parseInt(feesDto.getFeesHistoryDto().getPaidAmount())
							+ Integer.parseInt(dto.getFeesHistoryDto().getPaidAmount())));
			updateDto.setFeesStatus(FeesConstant.Pending.toString());
			updateDto.getFeesHistoryDto().setId(null);
			updateDto.setCourseName(null);
			updateDto.setMailSendStatus(dto.getMailSendStatus());
			updateDto.setReminderDate(dto.getReminderDate());
			updateDto.setSoftFlag(dto.getSoftFlag());
			updateDto.setTotalAmount(null);

			try {

				int index = findIndex(feesDto.getFeesHistoryDto().getEmail());
				String followupRanges = feesFinalDtoRanges.getFeesUpdateStartRange() + index
						+ feesFinalDtoRanges.getFeesUpdateEndRange() + index;
				System.err.println(updateDto);
				feesRepository.updateFeesDetiles(followupRanges, extractDtoDetails(updateDto));
				feesCacheService.updateCacheIntoFeesDetils(CacheConstant.getFeesDetils.toString(),
						CacheConstant.allDetils.toString(), updateDto.getFeesHistoryDto().getEmail(),
						extractDtoDetails(updateDto));
				log.info("FeesDetiles Updated Sucessfully");
				return "Feesfollowup and feesDetiles Updated Sucessfully";

			} catch (Exception e) {
				log.error("fees Detiles Cannot be updated some exception is there");

			}
		}
		return "Email is miss matching data could not be updated ";
	}

	private void setToFeesHistory(String fees, FeesDto dto) {
		FeesHistoryDto feesHistoryDto = new FeesHistoryDto();
		feesHistoryDto.setFeesfollowupDate(LocalDate.now().toString());
		feesHistoryDto.setEmail(dto.getFeesHistoryDto().getEmail());
		feesHistoryDto.setLastFeesPaidDate(dto.getFeesHistoryDto().getLastFeesPaidDate());
		feesHistoryDto.setFollowupCallbackDate(dto.getFeesHistoryDto().getFollowupCallbackDate());
		feesHistoryDto.setPaymentMode(dto.getFeesHistoryDto().getPaymentMode());
		feesHistoryDto.setTransectionId(dto.getFeesHistoryDto().getTransectionId());
		feesHistoryDto.setPaidAmount(fees);
		feesHistoryDto.setPaidTo(dto.getFeesHistoryDto().getPaidTo());
		feesHistoryDto.setId(null);
		feesRepository.updateDetilesToFollowUp(feesFinalDtoRanges.getFeesUpdateRange(),
				extractDtoDetails(feesHistoryDto));
		feesCacheService.addFeesFollowUpIntoCache(CacheConstant.getFolllowUpdata.toString(),
				CacheConstant.feesfollowUpData.toString(), extractDtoDetails(feesHistoryDto));
	}

}
