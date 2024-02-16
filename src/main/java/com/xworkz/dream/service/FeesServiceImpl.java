package com.xworkz.dream.service;

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
import com.xworkz.dream.dto.utils.FeesUtils;
import com.xworkz.dream.dto.utils.FeesWithHistoryDto;
import com.xworkz.dream.dto.utils.WrapperUtil;
import com.xworkz.dream.feesDtos.EmailList;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;
import com.xworkz.dream.feesDtos.FeesUiDto;
import com.xworkz.dream.feesDtos.SheetFeesDetiles;
import com.xworkz.dream.repository.FeesRepository;
import com.xworkz.dream.repository.FollowUpRepository;
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

	@Override
	public String writeFeesDetails(FeesUiDto dto, String feesEmailRange) {
		log.info("Running service writeFeesDetails with input: {}", dto);
		EmailList duplicateEntry = null;
		List<List<Object>> emails = feesRepository.getEmailList(feesEmailRange);
		if (emails != null) {
			duplicateEntry = emails.stream().map(feesWrapper::listToEmail)
					.filter(email -> email.getEmail().equalsIgnoreCase(dto.getEmail())).findFirst().orElse(null);
			if (duplicateEntry == null) {
				boolean write;
				write = feesRepository.writeFeesDetails(util.extractDtoDetails(feesUtils.feesDtosetValues(dto)));
				if (write == true) {
					feesCacheService.addNewFeesDetilesIntoCache(CacheConstant.getFeesDetails.toString(),
							CacheConstant.allDetails.toString(),
							util.extractDtoDetails(feesUtils.feesDtosetValues(dto)));
					feesCacheService.addEmailToCache(CacheConstant.getFeesEmail.toString(),
							CacheConstant.email.toString(), dto.getEmail());
					log.info("Fees details saved successfully: {}", dto);
					return "feesDetiles Saved sucessfully";
				}

				log.error("Failed to save fees details: {}", dto);
				return "Failed to save fees details";
			}

		} else {
			boolean write;
			write = feesRepository.writeFeesDetails(util.extractDtoDetails(feesUtils.feesDtosetValues(dto)));

			if (write == true) {
				feesCacheService.addNewFeesDetilesIntoCache(CacheConstant.getFeesDetails.toString(),
						CacheConstant.allDetails.toString(), util.extractDtoDetails(feesUtils.feesDtosetValues(dto)));
				feesCacheService.addEmailToCache(CacheConstant.getFeesEmail.toString(), CacheConstant.email.toString(),
						dto.getEmail());
				log.info("Fees details saved successfully: {}", dto);
				return "feesDetiles Saved sucessfully";
			}

			log.error("Failed to save fees details: {}", dto);
			return "Failed to save fees details";

		}
		return "Failed to save fees details already Exists";

	}

	@Override
	public SheetFeesDetiles getAllFeesDetails(String getFeesDetilesRange, String minIndex, String maxIndex, String date,
			String courseName, String paymentMode) {
		List<FeesDto> convertingListToDto = feesRepository.getAllFeesDetiles(getFeesDetilesRange).stream()
				.map(feesWrapper::listToFeesDTO)
				.filter(dto -> dto.getSoftFlag().equalsIgnoreCase(ServiceConstant.ACTIVE.toString()))
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
		log.debug("Update Dto is {}", feesDto);
		if (0L != feesDtos.get(0).getBalance()) {
			if ((int) Long.parseLong(feesDtos.get(0).getFeesHistoryDto().getPaidAmount())
					+ (int) Long.parseLong(feesDto.getFeesHistoryDto().getPaidAmount()) <= feesDtos.get(0)
							.getTotalAmount().intValue()) {
				feesDtos.stream().forEach(dto -> {
					try {
						util.upateValuesSet(feesDto, dto);
					} catch (Exception e) {
						log.error("Error updating values in FeesDto: {}", e);

					}
				});
				log.info("Data updated successfully");
				return "Data updated successfully";
			}
		}
		return "Update data Error";
	}

	public List<FeesDto> getFeesDetailsByemail(String email, String getFeesDetilesRange) {
		List<FeesDto> filteredDtos = feesRepository.getAllFeesDetiles(getFeesDetilesRange).stream()
				.map(feesWrapper::listToFeesDTO)
				.filter(dto -> dto.getFeesHistoryDto().getEmail().equalsIgnoreCase(email)).collect(Collectors.toList());
		log.info("Fees details fetched successfully for email: {}", email);
		return filteredDtos;
	}

	@Override
	public String transForData(String id, String feesEmailRange) {
		followUpRepository.getFollowUpDetails(id).stream().map(dreamWrapper::listToFollowUpDTO)
				.filter(feesDto -> feesDto.getCurrentStatus().equalsIgnoreCase(Status.Joined.toString())
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

}
