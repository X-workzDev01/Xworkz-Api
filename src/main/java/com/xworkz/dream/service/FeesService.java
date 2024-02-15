package com.xworkz.dream.service;

import com.xworkz.dream.dto.utils.FeesWithHistoryDto;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesUiDto;
import com.xworkz.dream.feesDtos.SheetFeesDetiles;

public interface FeesService {

	String writeFeesDetiles(FeesUiDto dto, String feesEmailRange);

	SheetFeesDetiles getAllFeesDetiles(String getFeesDetiles, String minIndex, String maxIndex, String date,
			String batch, String paymentMode);

	FeesWithHistoryDto getDetilesByEmail(String email, String getFeesDetilesRange, String getFeesDetilesfollowupRange);

	String updateFeesFollowUp(FeesDto dto, String feesDetiles);

	String transForData(String id, String feesEmailRange);

}
