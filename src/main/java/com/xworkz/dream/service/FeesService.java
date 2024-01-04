package com.xworkz.dream.service;

import java.io.IOException;

import com.xworkz.dream.dto.utils.FeesWithHistoryDto;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesUiDto;
import com.xworkz.dream.feesDtos.SheetFeesDetiles;

public interface FeesService {

	String writeFeesDetiles(FeesUiDto dto,String feesEmailRange) throws IOException, IllegalAccessException;

	SheetFeesDetiles getAllFeesDetiles(String getFeesDetiles, String minIndex, String maxIndex, String date,
			String batch, String paymentMode) throws IOException;

	FeesWithHistoryDto getDetilesByEmail(String email, String getFeesDetilesRange, String getFeesDetilesfollowupRange)
			throws IOException;

	String updateFeesFollowUp(FeesDto dto, String feesDetiles, String range) throws IOException;

}
