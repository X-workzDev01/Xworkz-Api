package com.xworkz.dream.service;

import com.xworkz.dream.dto.utils.FeesWithHistoryDto;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesUiDto;
import com.xworkz.dream.feesDtos.SheetFeesDetiles;

public interface FeesService {

	String writeFeesDetails(FeesUiDto dto, String feesEmailRange);

	SheetFeesDetiles getAllFeesDetails(String getFeesDetiles, String minIndex, String maxIndex, String date,
			String batch, String paymentMode,String status);

	FeesWithHistoryDto getDetailsByEmail(String email, String getFeesDetilesRange, String getFeesDetilesfollowupRange);

	String updateFeesFollowUp(FeesDto dto, String feesDetiles);

	String transForData(String id, String feesEmailRange);

	String updateNameAndEmail(Integer feesConcession,String traineeName, String oldEmail, String newEmail, String updatedBy);

}
