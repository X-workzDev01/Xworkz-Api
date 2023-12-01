package com.xworkz.dream.wrapper;

import java.io.IOException;
import java.util.List;

import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;

public interface FeesDetilesWrapper {
	FeesDto listToFeesDTO(List<Object> row) throws IOException;

	FeesHistoryDto getListToFeesHistoryDto(List<Object> row);

}
