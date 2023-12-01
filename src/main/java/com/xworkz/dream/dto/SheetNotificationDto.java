package com.xworkz.dream.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SheetNotificationDto {
	private List<FollowUpDto> yesterdayCandidates;
	private List<FollowUpDto> todayCandidates;
	private List<FollowUpDto> afterFourDayCandidates;

}
