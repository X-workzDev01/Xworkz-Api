package com.xworkz.dream.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.cache.FeesFollowUpCacheService;
import com.xworkz.dream.constants.CacheConstant;
import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.utils.FeesUtils;
import com.xworkz.dream.dto.utils.FeesWithHistoryDto;
import com.xworkz.dream.dto.utils.WrapperUtil;
import com.xworkz.dream.feesDtos.EmailList;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesFinalDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;
import com.xworkz.dream.feesDtos.FeesUiDto;
import com.xworkz.dream.feesDtos.SheetFeesDetiles;
import com.xworkz.dream.repository.FeesRepository;
import com.xworkz.dream.repository.FollowUpRepository;
import com.xworkz.dream.scheduler.FeesScheduler;
import com.xworkz.dream.wrapper.DreamWrapper;
import com.xworkz.dream.wrapper.FeesDetilesWrapper;

@Service

public class FeesServiceImpl implements FeesService {
	@Autowired
	private WrapperUtil util;
	@Autowired
	private FollowUpRepository followUpRepository;
	@Autowired
	private FeesRepository feesRepository;
	private Logger log = LoggerFactory.getLogger(FeesServiceImpl.class);
	@Autowired
	private FeesDetilesWrapper feesWrapper;
	@Autowired
	private FeesUtils feesUtils;
	@Autowired
	private FeesFollowUpCacheService feesCacheService;
	@Autowired
	private DreamWrapper dreamWrapper;
	@Autowired
	private FeesFinalDto feesFinalDtoRanges;

	@Override
	public String writeFeesDetails(FeesUiDto dto, String feesEmailRange) {
		log.info("Running service writeFeesDetails with input: {}", dto);
		EmailList duplicateEntry = emailExistsOrNot(dto.getEmail(), feesEmailRange);
		if (dto.getStatus().equalsIgnoreCase(Status.Joined.toString()) && duplicateEntry == null) {
			boolean write = feesRepository.writeFeesDetails(util.extractDtoDetails(feesUtils.feesDtosetValues(dto)));
			if (write == true) {
				feesCacheService.addNewFeesDetilesIntoCache(CacheConstant.getFeesDetails.toString(),
						CacheConstant.allDetails.toString(), util.extractDtoDetails(feesUtils.feesDtosetValues(dto)));
				feesCacheService.addEmailToCache(CacheConstant.getFeesEmail.toString(), CacheConstant.email.toString(),
						dto.getEmail());
				log.info("Fees details saved successfully: {}", dto);
				return "feesDetiles Saved sucessfully";
			}

		}
		log.error("Failed to save fees details: {}", dto);
		return "feesDetiles already exists";
	}

	@Override
	public SheetFeesDetiles getAllFeesDetails(String getFeesDetilesRange, String minIndex, String maxIndex, String date,
			String courseName, String paymentMode) {
		List<FeesDto> convertingListToDto;
		convertingListToDto = feesRepository.getAllFeesDetiles(getFeesDetilesRange).stream()
				.map(feesWrapper::listToFeesDTO)
				.filter(dto -> dto.getSoftFlag() != null
						&& dto.getSoftFlag().equalsIgnoreCase(ServiceConstant.ACTIVE.toString()))
				.collect(Collectors.toList());
		return feesUtils.getDataByselectedItems(minIndex, maxIndex, date, courseName, paymentMode, convertingListToDto);

	}

	@Override
	public FeesWithHistoryDto getDetailsByEmail(String email, String getFeesDetilesRange,
			String getFeesDetilesfollowupRange) {
		List<FeesDto> filteredDtos = getFeesDetailsByemail(email, getFeesDetilesRange);

		List<FeesHistoryDto> filteredData = feesRepository.getFeesDetilesByemailInFollowup(getFeesDetilesfollowupRange)
				.stream()
				.filter(items -> items != null && !items.isEmpty() && items.size() > 1 && items.get(1) != null
						&& items.get(1).toString().equalsIgnoreCase(email))
				.map(items -> feesWrapper.getListToFeesHistoryDto(items)).collect(Collectors.toList());
		return new FeesWithHistoryDto(filteredDtos, filteredData);
	}

	@Override
	public String updateFeesFollowUp(FeesDto feesDto, String getFeesDetilesRange) {
		List<FeesDto> feesDtos = getFeesDetailsByemail(feesDto.getFeesHistoryDto().getEmail(), getFeesDetilesRange);
		log.debug("Update fees Dto is {}", feesDto);
		if (0L != feesDtos.get(0).getBalance()) {
			if ((int) Long.parseLong(feesDtos.get(0).getFeesHistoryDto().getPaidAmount())
					+ (int) Long.parseLong(feesDto.getFeesHistoryDto().getPaidAmount()) <= feesDtos.get(0)
							.getTotalAmount().intValue()) {
				feesDtos.stream().forEach(dto -> {
					util.upateValuesSet(feesDto, dto);
				});
				log.info("Data updated successfully");
				return "Data updated successfully";
			}
		}
		return "Update data Error";
	}

	public List<FeesDto> getFeesDetailsByemail(String email, String getFeesDetilesRange) {
		EmailList checkEmail = emailExistsOrNot(email, feesFinalDtoRanges.getFeesEmailRange());
		if (checkEmail != null && checkEmail.getEmail() != null) {
			List<FeesDto> filteredDtos = feesRepository.getAllFeesDetiles(getFeesDetilesRange).stream()
					.map(feesWrapper::listToFeesDTO).filter(dto -> dto.getFeesHistoryDto() != null
							&& dto.getFeesHistoryDto().getEmail().equalsIgnoreCase(email))
					.collect(Collectors.toList());
			log.info("Fees details fetched successfully for email: {}", email);
			return filteredDtos;
		}
		return Collections.emptyList();

	}

	private EmailList emailExistsOrNot(String email, String feesEmailRange) {
		List<List<Object>> listEmails = feesRepository.getEmailList(feesEmailRange);
		if (listEmails != null) {
			return listEmails.stream().map(feesWrapper::listToEmail)
					.filter(emails -> emails.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
		}
		return null;

	}

	@Override
	public String transForData(String id, String feesEmailRange) {
		followUpRepository.getFollowUpDetails(id).stream().map(dreamWrapper::listToFollowUpDTO)
				.filter(feesDto -> feesDto.getCurrentStatus() != null
						&& feesDto.getCurrentStatus().equalsIgnoreCase(Status.Joined.toString())
						&& feesDto.getFlagSheet().equalsIgnoreCase(ServiceConstant.ACTIVE.toString()))
				.forEach(dto -> {
					FeesUiDto uiDto = new FeesUiDto();
					uiDto.setAdminDto(dto.getAdminDto());
					uiDto.setEmail(dto.getBasicInfo().getEmail());
					uiDto.setName(dto.getBasicInfo().getTraineeName());
					uiDto.setStatus(dto.getCurrentStatus());
					uiDto.setLateFees(0L);
					writeFeesDetails(uiDto, feesEmailRange);

				});
		return "Transforred Data succesfully";

	}

	public String updateNameAndEmail(String name, String oldEmail, String newEmail, String updatedBy) {
		FeesDto dto = new FeesDto(new FeesHistoryDto(), new AuditDto());
		dto.getFeesHistoryDto().setEmail(oldEmail);
		dto.setName(name);
		dto.getAdmin().setUpdatedBy(updatedBy);
		FeesWithHistoryDto dtos = getDetailsByEmail(dto.getFeesHistoryDto().getEmail(),
				feesFinalDtoRanges.getGetFeesDetilesRange(), feesFinalDtoRanges.getGetFeesDetilesfollowupRange());
		util.upateValuesSet(dto, dtos.getFeesDto().get(0));
		return "Updated Sucessfully";
	}

}
