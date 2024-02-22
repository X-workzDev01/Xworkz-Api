package com.xworkz.dream.dto.utils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
 
import com.xworkz.dream.constants.FeesConstant;
import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;
import com.xworkz.dream.feesDtos.FeesUiDto;
import com.xworkz.dream.feesDtos.SheetFeesDetiles;
import com.xworkz.dream.service.BatchService;
import com.xworkz.dream.service.RegistrationService;

@Service
public class FeesUtils {
	@Value("${login.sheetId}")
	private String spreadSheetId;
	@Autowired
	private RegistrationService registrationService;
	@Autowired
	private BatchService batchService;

	private Logger log = LoggerFactory.getLogger(FeesUtils.class);

	public BatchDetailsDto getBatchDetiles(String email) {
		TraineeDto traineeDto;
		try {
			traineeDto = registrationService.getDetailsByEmail(spreadSheetId, email);

			if (traineeDto != null) {
				BatchDetailsDto details = batchService.getBatchDetailsByCourseName(spreadSheetId,
						traineeDto.getCourseInfo().getCourse());
				return details;
			}
		} catch (IOException e) {
			log.error("Batch detils is empty {} ", e);
		}
		return new BatchDetailsDto();

	}

	public String getTraineeDetiles(String email) {
		TraineeDto traineeDto = registrationService.getDetailsByEmail(spreadSheetId, email);
		if (traineeDto.getCourseInfo().getOfferedAs()
				.equalsIgnoreCase(FeesConstant.CSR_OFFERED.toString().replace('_', ' '))) {
			return traineeDto.getBasicInfo().getEmail();
		}
		return null;
	}

	public SheetFeesDetiles getDataByselectedItems(String minIndex, String maxIndex, String date, String courseName,
			String paymentMode, List<FeesDto> convertingListToDto, String status) {
		System.err.println(status + courseName);
		Predicate<FeesDto> predicate = null;
		if (!status.equalsIgnoreCase(ServiceConstant.NULL.toString()) && date.equals("null")
				&& courseName.equals("null") && paymentMode.equals("null")) {
			predicate = feesDto -> feesDto.getFeesStatus().equalsIgnoreCase(status);
		}
		if (!status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& !date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesStatus().equalsIgnoreCase(status)
					&& feesDto.getFeesHistoryDto().getFollowupCallbackDate().equalsIgnoreCase(date);
		}
		if (!status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesStatus().equalsIgnoreCase(status)
					&& feesDto.getCourseName().equalsIgnoreCase(courseName);
		}
		if (!status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesStatus().equalsIgnoreCase(status)
					&& feesDto.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode);
		}
		if (!status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& !date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesStatus().equalsIgnoreCase(status)
					&& feesDto.getFeesHistoryDto().getFollowupCallbackDate().equalsIgnoreCase(date)
					&& feesDto.getCourseName().equalsIgnoreCase(courseName);
		}
		if (!status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& !date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesStatus().equalsIgnoreCase(status)
					&& feesDto.getFeesHistoryDto().getFollowupCallbackDate().equalsIgnoreCase(date)
					&& feesDto.getCourseName().equalsIgnoreCase(courseName)
					&& feesDto.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode);
		}
		if (!status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& !date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesStatus().equalsIgnoreCase(status)
					&& feesDto.getFeesHistoryDto().getFollowupCallbackDate().equalsIgnoreCase(date)
					&& feesDto.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode);
		}
		if (!status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesStatus().equalsIgnoreCase(status)
					&& feesDto.getCourseName().equalsIgnoreCase(courseName)
					&& feesDto.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode);
		}
		if (status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& !date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesHistoryDto().getFollowupCallbackDate().equalsIgnoreCase(date);
		}
		if (status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& !date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesHistoryDto().getFollowupCallbackDate().equalsIgnoreCase(date)
					&& feesDto.getCourseName().equalsIgnoreCase(courseName);
		}
		if (status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& !date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesStatus().equalsIgnoreCase(status)
					&& feesDto.getFeesHistoryDto().getFollowupCallbackDate().equalsIgnoreCase(date)
					&& feesDto.getCourseName().equalsIgnoreCase(courseName)
					&& feesDto.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode);
		}
		if (status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getCourseName().equalsIgnoreCase(courseName);
		}

		if (status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode);
		}
		if (status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& !date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode)
					&& feesDto.getFeesHistoryDto().getFollowupCallbackDate().equalsIgnoreCase(date);

		}
		if (status.equalsIgnoreCase(ServiceConstant.NULL.toString())
				& date.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&&!courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
				&& !paymentMode.equalsIgnoreCase(ServiceConstant.NULL.toString())) {
			predicate = feesDto -> feesDto.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode)
					&& feesDto.getCourseName().equalsIgnoreCase(courseName);

		}
		if (predicate != null) {
			List<FeesDto> filteredList = convertingListToDto.stream().filter(predicate)
					.sorted(Comparator.comparing(FeesDto::getId).reversed()).collect(Collectors.toList());
			List<FeesDto> paginationData = filteredList.stream().skip(Long.valueOf(minIndex))
					.limit(Long.valueOf(maxIndex)).collect(Collectors.toList());
			return new SheetFeesDetiles(paginationData, filteredList.size());
		}
		return new SheetFeesDetiles(Collections.emptyList(), 0);
	}

	public FeesDto feesDtosetValues(FeesUiDto uidto) {
		FeesDto feesDto = new FeesDto();
		BatchDetailsDto details = getBatchDetiles(uidto.getEmail());
		return setFeesDetilesDto(uidto, feesDto, details);
	}

	public FeesDto setFeesDetilesDto(FeesUiDto uiDto, FeesDto feesDto, BatchDetailsDto details) {
		FeesDto feesDtos = new FeesDto(new FeesHistoryDto(), new AuditDto());
		if (uiDto != null && uiDto.getStatus() != null
				&& uiDto.getStatus().equalsIgnoreCase(Status.Joined.toString())) {
			if (uiDto.getEmail() != null) {
				feesDtos.getFeesHistoryDto().setEmail(uiDto.getEmail());
				feesDtos.getFeesHistoryDto().setTransectionId(FeesConstant.NA.toString());
				feesDtos.setName(uiDto.getName());
				feesDtos.getAdmin().setCreatedBy(uiDto.getAdminDto().getCreatedBy());
				feesDtos.getAdmin().setCreatedOn(uiDto.getAdminDto().getCreatedOn());
				feesDtos.getAdmin().setUpdatedBy(FeesConstant.NA.toString());
				feesDtos.getAdmin().setUpdatedOn(FeesConstant.NA.toString());
				feesDtos.setFeesStatus(FeesConstant.FREE.toString());
				feesDtos.setLateFees(0L);
				feesDtos.setFeeConcession(0);
				feesDtos.getFeesHistoryDto().setFeesfollowupDate(FeesConstant.NA.toString());
				feesDtos.getFeesHistoryDto().setPaidAmount("0");
				feesDtos.getFeesHistoryDto().setLastFeesPaidDate(FeesConstant.NA.toString());
				feesDtos.setSoftFlag(ServiceConstant.ACTIVE.toString());
				feesDtos.getFeesHistoryDto().setPaymentMode(FeesConstant.NA.toString());
				feesDtos.setMailSendStatus(FeesConstant.NO.toString());
				feesDtos.setComments(FeesConstant.NA.toString());
				if (details != null) {
					feesDtos.setReminderDate(LocalDate.parse(details.getStartDate()).plusDays(28).toString());
					feesDtos.getFeesHistoryDto().setPaidTo(FeesConstant.NA.toString());
					feesDtos.getFeesHistoryDto()
							.setFollowupCallbackDate(LocalDate.parse(details.getStartDate()).plusDays(1).toString());
				}
				log.debug("After setting Followup dto is {}", feesDtos);
				return feesDtos;
			}

		}
		return new FeesDto();

	}

}
