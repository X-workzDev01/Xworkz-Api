package com.xworkz.dream.dto.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.cache.FeesFollowUpCacheService;
import com.xworkz.dream.constants.CacheConstant;
import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesFinalDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;
import com.xworkz.dream.repository.FeesRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.wrapper.DreamWrapper;

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
	@Autowired
	private DreamWrapper dreamWrapper;
	@Autowired
	private RegisterRepository registerRepository;

	public void updateBasedOnEditEmail(Integer feesConcession, String traineeName, String oldEmail, String newEmail,
			String updatedBy, FeesDto existingDto, List<FeesHistoryDto> listOfHistory) {
		if (!existingDto.getFeesHistoryDto().getEmail().equalsIgnoreCase(newEmail)
				|| existingDto.getFeeConcession() != feesConcession
				|| !existingDto.getName().equalsIgnoreCase(traineeName)) {
			FeesDto updateDto = new FeesDto();
			updateDto.setFeeConcession(feesConcession);
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
			feesCache.updateFeesCacheIntoEmail(CacheConstant.feesFollowUpEmailRange.toString(),
					CacheConstant.feesFollowUpEmail.toString(), oldEmail, newEmail);
			updateDto.getFeesHistoryDto().setId(historyDto.getId());
			feesCache.updateCacheIntoFeesFollowUp(CacheConstant.getFeesFolllowUpdata.toString(),
					CacheConstant.feesfollowUpData.toString(), oldEmail,
					util.extractDtoDetails(updateDto.getFeesHistoryDto()));

		});
	}

	public List<FollowUpDto> getFollowupList(List<List<Object>> followUpList) {
		List<List<Object>> traineeData = registerRepository.readData(feesFinalDtoRanges.getId());
		List<FollowUpDto> filteredFollowUp = new ArrayList<FollowUpDto>();
		if (filteredFollowUp != null) {
			followUpList.stream().map(dreamWrapper::listToFollowUpDTO)
					.filter(dto -> dto.getFlagSheet().equalsIgnoreCase(ServiceConstant.ACTIVE.toString()))
					.forEach(followupDto -> {

						TraineeDto traineeDto = getTraineeDtoByEmail(traineeData,
								followupDto.getBasicInfo().getEmail());
						if (traineeDto != null) {
							followupDto.setCourseName(traineeDto.getCourseInfo().getCourse());
							followupDto.setYear(traineeDto.getEducationInfo().getYearOfPassout());
							followupDto.setCollegeName(traineeDto.getEducationInfo().getCollegeName());
							filteredFollowUp.add(followupDto);
						}
					});
		}
		return filteredFollowUp;
	}

	public TraineeDto getTraineeDtoByEmail(List<List<Object>> traineeData, String email) {
		if (traineeData == null || email == null) {
			return null;
		}
		return traineeData.stream()
				.filter(row -> row.size() > 2 && row.get(2) != null && row.get(2).toString().equalsIgnoreCase(email))
				.map(dreamWrapper::listToDto).findFirst().orElse(null);
	}

}
