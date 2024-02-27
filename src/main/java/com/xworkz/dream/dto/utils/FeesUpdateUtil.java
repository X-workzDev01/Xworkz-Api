package com.xworkz.dream.dto.utils;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.cache.FeesFollowUpCacheService;
import com.xworkz.dream.constants.CacheConstant;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesFinalDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;
import com.xworkz.dream.repository.FeesRepository;

@Service
public class FeesUpdateUtil {
	@Autowired
	private FeesFinalDto feesFinalDtoRanges;
	@Autowired
	private WrapperUtil util;
	@Autowired
	private FeesRepository feesRepository;
	@Autowired
	private FeesFollowUpCacheService feesCache;
	private Logger log = LoggerFactory.getLogger(FeesUpdateUtil.class);

	public void updateBasedOnEditEmail(Integer feesConcession, String traineeName, String oldEmail, String newEmail,
			String updatedBy, FeesDto existingDto, List<FeesHistoryDto> listOfHistory) {
		if (!existingDto.getFeesHistoryDto().getEmail().equalsIgnoreCase(newEmail)
				|| existingDto.getFeeConcession() != feesConcession
				|| !existingDto.getName().equalsIgnoreCase(traineeName)) {
			FeesDto updateDto = new FeesDto();
			if (feesConcession != 0) {
				updateDto.setFeeConcession(feesConcession);
			}
			updateDto.setId(existingDto.getId()); 
			updateDto.setFeesHistoryDto(existingDto.getFeesHistoryDto());
			updateDto.getFeesHistoryDto().setEmail(newEmail);
			updateDto.setName(traineeName);
			updateDto.setAdmin(existingDto.getAdmin());
			updateDto.getAdmin().setUpdatedBy(updatedBy);
			updateDto.getAdmin().setUpdatedOn(LocalDateTime.now().toString());
			updateDto.setReminderDate(existingDto.getReminderDate());
			updateDto.setFeesStatus(existingDto.getFeesStatus());
			updateDto.setMailSendStatus(existingDto.getMailSendStatus());
			updateDto.setComments(existingDto.getComments());
			updateDto.getFeesHistoryDto().setId(null);
			updateDto.setLateFees(existingDto.getLateFees());
			updateDto.setSoftFlag(existingDto.getSoftFlag());
			updateToFeesDetiles(oldEmail, newEmail, updateDto, listOfHistory);
			log.info("fees Email updated successfully   {}     {}    {}", newEmail, traineeName, feesConcession);
		}
	}

	private void updateToFeesDetiles(String oldEmail, String newEmail, FeesDto updateDto,
			List<FeesHistoryDto> listOfFeesHistory) {
		int index = util.findIndex(oldEmail);
		String followupRanges = feesFinalDtoRanges.getFeesUpdateStartRange() + index
				+ feesFinalDtoRanges.getFeesUpdateEndRange() + index;
		feesRepository.updateFeesDetiles(followupRanges, util.extractDtoDetails(updateDto));
		feesCache.updateCacheIntoFeesDetils(CacheConstant.getFeesDetails.toString(),
				CacheConstant.allDetails.toString(), oldEmail, util.extractDtoDetails(updateDto));
		feesCache.updateFeesCacheIntoEmail(CacheConstant.getFeesEmail.toString(), CacheConstant.email.toString(),
				oldEmail, newEmail);
		updateFeesFollowUp(oldEmail, newEmail, updateDto, listOfFeesHistory);
	}

	private void updateFeesFollowUp(String oldEmail, String newEmail, FeesDto updateDto,
			List<FeesHistoryDto> listOfFeesHistory) {
		listOfFeesHistory.stream().forEach(historyDto -> {
			int followUpIndexindex = util.findIndexFollowUp(historyDto.getEmail());
			String followupRange = feesFinalDtoRanges.getFeesUpdateRange() + followUpIndexindex
					+ feesFinalDtoRanges.getFeesUpdateEndRange() + followUpIndexindex;
			feesRepository.updateFeesFollowUpByEmail(followupRange,
					util.extractDtoDetails(updateDto.getFeesHistoryDto()));
			feesCache.updateFeesCacheIntoEmail(CacheConstant.followUpEmailRange.toString(),
					CacheConstant.followUpEmail.toString(), oldEmail, newEmail);
			updateDto.getFeesHistoryDto().setId(historyDto.getId());
			feesCache.updateCacheIntoFeesFollowUp(CacheConstant.getFeesFolllowUpdata.toString(),
					CacheConstant.feesfollowUpData.toString(), oldEmail,
					util.extractDtoDetails(updateDto.getFeesHistoryDto()));

		});
	}

}
