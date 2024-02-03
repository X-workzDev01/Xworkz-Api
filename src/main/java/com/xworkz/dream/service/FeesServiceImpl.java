package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.utils.FeesUtils;
import com.xworkz.dream.dto.utils.FeesWithHistoryDto;
import com.xworkz.dream.dto.utils.WrapperUtil;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;
import com.xworkz.dream.feesDtos.FeesUiDto;
import com.xworkz.dream.feesDtos.SheetFeesDetiles;
import com.xworkz.dream.repository.FeesRepository;
import com.xworkz.dream.wrapper.FeesDetilesWrapper;

@Service

public class FeesServiceImpl implements FeesService {

	@Autowired
	private WrapperUtil util;
	@Autowired
	private FeesRepository feesRepository;
	private Logger log = LoggerFactory.getLogger(FeesServiceImpl.class);
	@Autowired
	private FeesDetilesWrapper feesWrapper;
	@Autowired
	private FeesUtils feesUtils;

	@Override

	public String writeFeesDetiles(FeesUiDto dto, String feesEmailRange) throws IOException, IllegalAccessException {
		log.info("Running service writeFeesDetiles with input: {}", dto);
		boolean duplicateEntry = feesRepository.getEmailList(feesEmailRange).values().stream()
				.anyMatch(items -> items.toString().contains(dto.getEmail()));
		if (duplicateEntry == false) {
			boolean write = feesRepository.writeFeesDetiles(util.extractDtoDetails(feesUtils.feesDtosetValues(dto)));
			if (write == true) {
				log.info("Fees details saved successfully: {}", dto);
				return "feesDetiles Saved sucessfully";
			}
			log.error("Failed to save fees details: {}", dto);
			return "Failed to save fees details";
		} else {
			log.warn("Data save failed - Details already exist: {}", dto);

			return "Failed to save fees details already Exists";
		}
	}

	@Override
	public SheetFeesDetiles getAllFeesDetiles(String getFeesDetilesRange, String minIndex, String maxIndex, String date,
			String courseName, String paymentMode) throws IOException {
        log.info("Read data from the repository for feesDto details");
		List<FeesDto> convertingListToDto = feesRepository.getAllFeesDetiles(getFeesDetilesRange).stream()
				.filter(items -> items.toString().contains("Active")).map(items -> {
					try {
						return feesWrapper.listToFeesDTO(items);
					} catch (IOException e) {
                        log.error("List converting error", e.getMessage(), e);
						return new FeesDto();
					}
				}).collect(Collectors.toList());
        log.info("Return data after filter process for feesDto details");
		return feesUtils.getDataByselectedItems(minIndex, maxIndex, date, courseName, paymentMode, convertingListToDto);

	}

	@Override
	public FeesWithHistoryDto getDetilesByEmail(String email, String getFeesDetilesRange,
			String getFeesDetilesfollowupRange) throws IOException {
		log.info("start getting detilsBy email");
		List<FeesDto> filteredDtos = getFeesDetilesByemail(email, getFeesDetilesRange);

		List<FeesHistoryDto> filteredData = feesRepository.getFeesDetilesByemailInFollowup(getFeesDetilesfollowupRange)
				.stream()
				.filter(items -> items != null && !items.isEmpty() && items.size() > 1 && items.get(1) != null
						&& items.get(1).toString().contains(email))
				.map(items -> feesWrapper.getListToFeesHistoryDto(items)).collect(Collectors.toList());
        log.info("Details retrieved successfully by email");
		return new FeesWithHistoryDto(filteredDtos, filteredData);
	}

	@Override
	public String updateFeesFollowUp(FeesDto feesDto, String getFeesDetilesRange, String range) throws IOException {
	    log.info("Entering the update in fees follow-up");
		List<FeesDto> feesDtos = getFeesDetilesByemail(feesDto.getFeesHistoryDto().getEmail(), getFeesDetilesRange);
		log.info("Enter The update  in fees followup");
        log.debug("Update Dto is {}", feesDto);
		if (0L != feesDtos.get(0).getBalance()) {
			if ((int) Long.parseLong(feesDtos.get(0).getFeesHistoryDto().getPaidAmount())
					+ (int) Long.parseLong(feesDto.getFeesHistoryDto().getPaidAmount()) <= feesDtos.get(0)
							.getTotalAmount().intValue()) {
				feesDtos.stream().forEach(dto -> {
					try {
						util.upateValuesSet(feesDto, dto);
					} catch (IllegalAccessException e) {
                        log.error("Error updating values in FeesDto: {}", e.getMessage(), e);
					} catch (IOException e) {
                        log.error("IOException during value update: {}", e.getMessage(), e);
					}
				});
                log.info("Data updated successfully");
				return "Data updated successfully";
			}
		}
		return "Update data Error";
	}

	public List<FeesDto> getFeesDetilesByemail(String email, String getFeesDetilesRange) throws IOException {
		List<FeesDto> filteredDtos = feesRepository.getAllFeesDetiles(getFeesDetilesRange).stream()
				.filter(items -> items != null && items.size() > 2 && items.get(2) != null 
						&& items.get(2).toString().equalsIgnoreCase(email))
				.map(items -> {
					try {
						FeesDto dto = feesWrapper.listToFeesDTO(items);
						return dto;
					} catch (IOException e) {
						log.error("Error converting list to FeesDto", e);
						return null;
					}
				}).collect(Collectors.toList());
        log.info("Fees details fetched successfully for email: {}", email);
		return filteredDtos;
	}

}
