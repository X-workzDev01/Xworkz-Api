package com.xworkz.dream.scheduler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.BatchDetails;
import com.xworkz.dream.dto.utils.FeesUtils;
import com.xworkz.dream.dto.utils.WrapperUtil;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.repository.FeesRepository;
import com.xworkz.dream.wrapper.FeesDetilesWrapper;

@Service
public class FeesSchedulerImpl implements FeesScheduler {

	@Autowired
	private FeesUtils feesUtil;
	@Autowired
	private FeesRepository feesRepository;
	@Autowired
	private FeesDetilesWrapper feesWrapper;
	@Autowired
	private WrapperUtil util;
	@Value("${sheets.getFeesDetiles}")
	private String getFeesDetilesRange;
	private Logger log = LoggerFactory.getLogger(FeesSchedulerImpl.class);

	@Override
	@Scheduled(fixedRate = 12 * 60 * 60 * 1000)
	public String afterFreeCourseCompletedChengeFeesStatus() throws IOException {
		log.info("Scheduler running After free Course");
		List<List<Object>> getAllFeesDetiles = feesRepository.getAllFeesDetiles(getFeesDetilesRange);
		getAllFeesDetiles.stream().filter(
				items -> items != null && items.size() > 2 && items.get(2) != null && items.contains("Active"))
				.map(items -> {
					try {
						FeesDto dto = feesWrapper.listToFeesDTO(items);
						if (dto.getFeesHistoryDto().getEmail()
								.equalsIgnoreCase(feesUtil.getTraineeDetiles(dto.getFeesHistoryDto().getEmail()))) {
							BatchDetails detiles = feesUtil.getBatchDetiles(dto.getFeesHistoryDto().getEmail());
							updateCSRofferedAfterFreeTraining(dto, detiles);
							return null;
						} else {
							BatchDetails detiles = feesUtil.getBatchDetiles(dto.getFeesHistoryDto().getEmail());
							afterAMonthChangeStatusAutometically(dto, detiles);

							return null;
						}
					} catch (IOException | IllegalAccessException e) {
						log.error("Error processing fee details", e);
						return null;
					}
				}).collect(Collectors.toList());
		return null;
	}

	private FeesDto afterAMonthChangeStatusAutometically(FeesDto dto, BatchDetails detils)
			throws IOException, IllegalAccessException {
		if (dto.getFeesStatus().equalsIgnoreCase("FREE") && LocalDate.parse(detils.getStartDate()).plusDays(29)
				.isAfter(LocalDate.parse(detils.getStartDate()))) {
			dto.setFeesStatus("FEES_DUE");
			int index = util.findIndex(dto.getFeesHistoryDto().getEmail());
			String followupRanges = "FeesDetiles!B" + index + ":AB" + index;
			log.debug("Updating fees status. Range: {}, FeesDto: {}", followupRanges, dto);
			List<Object> list = util.extractDtoDetails(dto);
			list.remove(2);
			list.remove(11);
			list.remove(11);
			list.remove(20);
			list.remove(20);
			list.add("Active");
			feesRepository.updateFeesDetiles(followupRanges, list);
			log.info("Fees status updated successfully for email: {}", dto.getFeesHistoryDto().getEmail());

			return dto;
		}
		return dto;
	}

	private FeesDto updateCSRofferedAfterFreeTraining(FeesDto dto, BatchDetails detils)
			throws IOException, IllegalAccessException {
		if (dto.getFeesStatus().equalsIgnoreCase("FREE") && LocalDate.parse(detils.getStartDate()).plusDays(59)
				.isAfter(LocalDate.parse(detils.getStartDate()))) {
			dto.setFeesStatus("FEES_DUE");
			int index = util.findIndex(dto.getFeesHistoryDto().getEmail());
			String followupRanges = "FeesDetiles!B" + index + ":U" + index;
			List<Object> list = util.extractDtoDetails(dto);
			list.remove(2);
			list.remove(11);
			list.remove(11);
			list.remove(20);
			feesRepository.updateFeesDetiles(followupRanges, list);
			log.info("Fees status updated successfully for email: {}", dto.getFeesHistoryDto().getEmail());
			return dto;
		}
		return dto;
	}

}
