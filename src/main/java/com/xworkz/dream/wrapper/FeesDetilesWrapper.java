package com.xworkz.dream.wrapper;

import java.util.List;

import com.xworkz.dream.feesDtos.EmailList;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;

public interface FeesDetilesWrapper {
	FeesDto listToFeesDTO(List<Object> row);

	FeesHistoryDto getListToFeesHistoryDto(List<Object> row);

	EmailList listToEmail(List<Object> list);

}
