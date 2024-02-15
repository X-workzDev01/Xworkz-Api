package com.xworkz.dream.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xworkz.dream.cache.FeesFollowUpCacheService;
import com.xworkz.dream.constants.CacheConstant;
import com.xworkz.dream.constants.FeesConstant;
import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.utils.FeesUtils;
import com.xworkz.dream.dto.utils.WrapperUtil;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesFinalDto;
import com.xworkz.dream.repository.FeesRepository;
import com.xworkz.dream.wrapper.FeesDetilesWrapper;

@Service
public class FeesSchedulerImpl implements FeesScheduler {
	@Autowired
	private FeesFinalDto feesFinalDtoRanges;
	@Autowired
	private FeesUtils feesUtil;
	@Autowired
	private FeesRepository feesRepository;
	@Autowired
	private FeesDetilesWrapper feesWrapper;
	@Autowired
	private WrapperUtil util;
	@Autowired
	private FeesFollowUpCacheService feesCacheService;
	private Logger log = LoggerFactory.getLogger(FeesSchedulerImpl.class);

	@Override
	@Scheduled(fixedRate = 12 * 60 * 60 * 1000)
	public String afterFreeCourseCompletedChengeFeesStatus() {
		log.info("Scheduler running After free Course");
		feesRepository.getAllFeesDetiles(feesFinalDtoRanges.getGetFeesDetilesRange()).stream()
				.filter(items -> items != null && items.size() > 2 && items.get(2) != null
						&& items.contains(ServiceConstant.ACTIVE.toString()))
				.map(items -> {
					try {
						FeesDto dto = feesWrapper.listToFeesDTO(items);
						if (dto.getFeesHistoryDto().getEmail()
								.equalsIgnoreCase(feesUtil.getTraineeDetiles(dto.getFeesHistoryDto().getEmail()))) {
							BatchDetailsDto detiles = feesUtil.getBatchDetiles(dto.getFeesHistoryDto().getEmail());
							updateCSRofferedAfterFreeTraining(dto, detiles);
							return null;
						} else {
							BatchDetailsDto detiles = feesUtil.getBatchDetiles(dto.getFeesHistoryDto().getEmail());
							afterAMonthChangeStatusAutometically(dto, detiles);

							return null;
						}
					} catch (Exception e) {
						log.error("Fetching Detiles is not Found");
						return null;
					}
				}).collect(Collectors.toList());

		return null;
	}

	private FeesDto afterAMonthChangeStatusAutometically(FeesDto dto, BatchDetailsDto detiles) {
		if (dto.getFeesStatus().equalsIgnoreCase("FREE") && LocalDate.parse(detiles.getStartDate()).plusDays(29)
				.isAfter(LocalDate.parse(detiles.getStartDate()))) {
			dto.setFeesStatus("FEES_DUE");
			int index;
			try {
				index = util.findIndex(dto.getFeesHistoryDto().getEmail());

				String followupRanges = "FeesDetiles!B" + index + ":AB" + index;
				List<Object> list = util.extractDtoDetails(dto);
				list.remove(2);
				list.remove(11);
				list.remove(11);
				list.remove(20);
				list.remove(20);
				list.add(ServiceConstant.ACTIVE.toString());
				feesRepository.updateFeesDetiles(followupRanges, list);
				feesCacheService.updateCacheIntoFeesDetils(CacheConstant.getFeesDetils.toString(),
						CacheConstant.allDetils.toString(), dto.getFeesHistoryDto().getEmail(), list);
				return dto;
			} catch (Exception e) {
				log.error("Error Updatind data {} ", e);
			}
		}
		return dto;
	}

	private FeesDto updateCSRofferedAfterFreeTraining(FeesDto dto, BatchDetailsDto detiles) {
		if (dto.getFeesStatus().equalsIgnoreCase(FeesConstant.FREE.toString()) && LocalDate
				.parse(detiles.getStartDate()).plusDays(59).isAfter(LocalDate.parse(detiles.getStartDate()))) {
			dto.setFeesStatus(FeesConstant.FEES_DUE.toString());
			int index;
			try {
				index = util.findIndex(dto.getFeesHistoryDto().getEmail());

				String followupRanges = feesFinalDtoRanges.getFeesUpdateStartRange() + index
						+ feesFinalDtoRanges.getFeesUpdateEndRange() + index;
				List<Object> list = util.extractDtoDetails(dto);
				list.remove(2);
				list.remove(11);
				list.remove(11);
				list.remove(20);
				list.remove(20);
				list.add(ServiceConstant.ACTIVE.toString());
				feesRepository.updateFeesDetiles(followupRanges, list);
				feesCacheService.updateCacheIntoFeesDetils(CacheConstant.getFeesDetils.toString(),
						CacheConstant.allDetils.toString(), dto.getFeesHistoryDto().getEmail(), list);
				return dto;
			} catch (Exception e) {
				log.error("Error Updating data to csr after free training {}  ", e);
			}
		}
		return dto;
	}

}
