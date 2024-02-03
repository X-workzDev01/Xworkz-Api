package com.xworkz.dream.dto.utils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

	public BatchDetailsDto getBatchDetiles(String email) throws IOException {
		TraineeDto traineeDto = registrationService.getDetailsByEmail(spreadSheetId, email);
		//log.info("Finding Trainee detiles by course ");
		BatchDetailsDto details = batchService.getBatchDetailsByCourseName(spreadSheetId,
				traineeDto.getCourseInfo().getCourse());
		//log.info("Finding Batch detiles by course "); 
		return details;
	} 

	public String getTraineeDetiles(String email) throws IOException {
		//log.info("Finding Trainee detiles  ");
		TraineeDto traineeDto = registrationService.getDetailsByEmail(spreadSheetId, email);
		if (traineeDto.getCourseInfo().getOfferedAs().equalsIgnoreCase("CSR Offered")) {
			return traineeDto.getBasicInfo().getEmail();
		}
		return null;
	}

	public SheetFeesDetiles getDataByselectedItems(String minIndex, String maxIndex, String date, String courseName,
			String paymentMode, List<FeesDto> convertingListToDto) {
		if (!courseName.equals("null") && date.equals("null") && paymentMode.equals("null")) {
			log.info("Running filter By CourseName");
			List<FeesDto> listDto = convertingListToDto.stream()
					.filter(items -> items.getCourseName().equalsIgnoreCase(courseName)).collect(Collectors.toList());
			List<FeesDto> listDtos = listDto.stream().skip(Integer.parseInt(minIndex)).limit(Integer.parseInt(maxIndex))
					.sorted(Comparator.comparing(FeesDto::getId).reversed()).collect(Collectors.toList());
			return new SheetFeesDetiles(listDtos, listDto.size());
		} else if (!courseName.equals("null") && !date.equals("null") && paymentMode.equals("null")) {
			log.info("Running filter By CourseName and Date");
			List<FeesDto> listDtos = convertingListToDto.stream()
					.filter(items -> !items.getFeesHistoryDto().getFeesfollowupDate().toString().equalsIgnoreCase("NA")
							&& items.getCourseName().equalsIgnoreCase(courseName)
							&& LocalDate.parse(items.getFeesHistoryDto().getFeesfollowupDate())
									.isEqual(LocalDate.parse(date)))
					.collect(Collectors.toList());
			List<FeesDto> listDto = listDtos.stream().skip(Integer.parseInt(minIndex)).limit(Integer.parseInt(maxIndex))
					.sorted(Comparator.comparing(FeesDto::getId).reversed()).collect(Collectors.toList());
			return new SheetFeesDetiles(listDto, listDtos.size());
		} else if (courseName.equals("null") && !date.equals("null") && paymentMode.equals("null")) {
			log.info("Running filter By CourseName and Date");
			List<FeesDto> listDtos = convertingListToDto.stream()
					.filter(items -> !items.getFeesHistoryDto().getFeesfollowupDate().toString().equalsIgnoreCase("NA")
							&& LocalDate.parse(items.getFeesHistoryDto().getFeesfollowupDate())
									.isEqual(LocalDate.parse(date)))
					.collect(Collectors.toList());
			List<FeesDto> listDto = listDtos.stream().skip(Integer.parseInt(minIndex)).limit(Integer.parseInt(maxIndex))
					.sorted(Comparator.comparing(FeesDto::getId).reversed()).collect(Collectors.toList());
			return new SheetFeesDetiles(listDto, listDtos.size());
		} else if (courseName.equals("null") && !date.equals("null") && !paymentMode.equals("null")) {
			log.info("Running filter By CourseName and Date");
			List<FeesDto> listDtos = convertingListToDto.stream()
					.filter(items -> !items.getFeesHistoryDto().getFeesfollowupDate().toString().equalsIgnoreCase("NA")
							&& LocalDate.parse(items.getFeesHistoryDto().getFeesfollowupDate())
									.isEqual(LocalDate.parse(date))
							&& items.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode))
					.collect(Collectors.toList());
			List<FeesDto> listDto = listDtos.stream().skip(Integer.parseInt(minIndex)).limit(Integer.parseInt(maxIndex))
					.sorted(Comparator.comparing(FeesDto::getId).reversed()).collect(Collectors.toList());
			return new SheetFeesDetiles(listDto, listDtos.size());
		} else if (!courseName.equals("null") && date.equals("null") && !paymentMode.equals("null")) {
			log.info("Running filter By CourseName and paymentDate");
			List<FeesDto> listDtos = convertingListToDto.stream()
					.filter(items -> !items.getFeesHistoryDto().getFeesfollowupDate().toString().equalsIgnoreCase("NA")
							&& items.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode)
							&& items.getCourseName().equalsIgnoreCase(courseName))
					.collect(Collectors.toList());
			List<FeesDto> listDto = listDtos.stream().skip(Integer.parseInt(minIndex)).limit(Integer.parseInt(maxIndex))
					.sorted(Comparator.comparing(FeesDto::getId).reversed()).collect(Collectors.toList());
			return new SheetFeesDetiles(listDto, listDtos.size());
		} else if (courseName.equals("null") && date.equals("null") && !paymentMode.equals("null")) {
			log.info("Running filter By Fees Status");
			List<FeesDto> listDtos = convertingListToDto.stream()
					.filter(items -> items.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode))
					.collect(Collectors.toList());
			List<FeesDto> listDto = listDtos.stream().skip(Integer.parseInt(minIndex)).limit(Integer.parseInt(maxIndex))
					.sorted(Comparator.comparing(FeesDto::getId).reversed()).collect(Collectors.toList());
			return new SheetFeesDetiles(listDto, listDtos.size());
		} else if (!courseName.equals("null") && !date.equals("null") && !paymentMode.equals("null")) {
			log.info("Running filter By CourseName and Date and status");
			List<FeesDto> listDtos = convertingListToDto.stream()
					.filter(items -> !items.getFeesHistoryDto().getFeesfollowupDate().toString().equalsIgnoreCase("NA")
							&& LocalDate.parse(items.getFeesHistoryDto().getFeesfollowupDate()).isEqual(
									LocalDate.parse(date))
							&& items.getCourseName().equalsIgnoreCase(courseName)
							&& items.getFeesHistoryDto().getPaymentMode().equalsIgnoreCase(paymentMode))
					.collect(Collectors.toList());
			List<FeesDto> listDto = listDtos.stream().sorted(Comparator.comparing(FeesDto::getId).reversed())
					.skip(Integer.parseInt(minIndex)).limit(Integer.parseInt(maxIndex)).collect(Collectors.toList());
			return new SheetFeesDetiles(listDto, listDtos.size());
		} else if (courseName.equals("null") && date.equals("null") && paymentMode.equals("null")) {
			log.info("Running filter No filter");
			List<FeesDto> listDto = convertingListToDto.stream().sorted(Comparator.comparing(FeesDto::getId).reversed())
					.skip(Integer.parseInt(minIndex)).limit(Integer.parseInt(maxIndex)).collect(Collectors.toList());
			return new SheetFeesDetiles(listDto, convertingListToDto.size());

		}
		return new SheetFeesDetiles(Collections.emptyList(), 0);
	}

	public FeesDto feesDtosetValues(FeesUiDto uidto) throws IOException {
		FeesDto feesDto = new FeesDto();
		BatchDetailsDto details = getBatchDetiles(uidto.getEmail());
		return setFeesDetilesDto(uidto, feesDto, details);
	}

	public FeesDto setFeesDetilesDto(FeesUiDto uiDto, FeesDto feesDto, BatchDetailsDto details) {
		FeesDto feesDtos = new FeesDto(new FeesHistoryDto(), new AuditDto());
		if (uiDto != null && uiDto.getStatus() != null
				&& uiDto.getStatus().equalsIgnoreCase(Status.Joined.toString())) {
			if (uiDto.getEmail() != null) {
				log.info("set fees detiles by batch");
				feesDtos.getFeesHistoryDto().setEmail(uiDto.getEmail());
				feesDtos.getFeesHistoryDto().setTransectionId("NA");
				feesDtos.setName(uiDto.getName());
				feesDtos.getAdmin().setCreatedBy(uiDto.getAdminDto().getCreatedBy());
				feesDtos.getAdmin().setCreatedOn(uiDto.getAdminDto().getCreatedOn());
				feesDtos.getAdmin().setUpdatedBy("NA");
				feesDtos.getAdmin().setUpdatedOn("NA");
				feesDtos.setFeesStatus("FREE");
				feesDtos.setLateFees(0L);
				feesDtos.setFeeConcession(0);
				feesDtos.getFeesHistoryDto().setFeesfollowupDate("NA");
				feesDtos.getFeesHistoryDto().setPaidAmount("0");
				feesDtos.getFeesHistoryDto().setLastFeesPaidDate("NA");
				feesDtos.setSoftFlag("Active");
				feesDtos.getFeesHistoryDto().setPaymentMode("NA");
				feesDtos.setMailSendStatus("No");
				feesDtos.setComments("NA");
				feesDtos.setReminderDate(LocalDate.parse(details.getStartDate()).plusDays(28).toString());
				feesDtos.getFeesHistoryDto().setPaidTo("NA");
				feesDtos.getFeesHistoryDto()
						.setFollowupCallbackDate(LocalDate.parse(details.getStartDate()).plusDays(1).toString());
			}
		}
		log.debug("After setting Followup dto is {}", feesDtos);
		return feesDtos;
	}

}
