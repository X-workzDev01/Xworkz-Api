package com.xworkz.dream.dto;

import java.util.List;

import com.xworkz.dream.feesDtos.FeesDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FeesNotificationDto {

	private List<FeesDto> yesterdayCandidates;
	private List<FeesDto> todayCandidates;
	private List<FeesDto> afterFourDayCandidates;

}
