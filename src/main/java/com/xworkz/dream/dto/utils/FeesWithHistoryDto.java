package com.xworkz.dream.dto.utils;

import java.util.List;

import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@NoArgsConstructor
@Getter
@Setter
public class FeesWithHistoryDto {

	private List<FeesDto> feesDto;
	private List<FeesHistoryDto> feesHistoryDto;

}
