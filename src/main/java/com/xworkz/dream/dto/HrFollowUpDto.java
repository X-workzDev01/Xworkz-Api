package com.xworkz.dream.dto;

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
public class HrFollowUpDto {
	private Integer id;
	private Integer hrId;
	private String attemptOn;
	private String attemptBy;
	private String attemptStatus;
	private String callDuration;
	private String callBackDate;
	private String callBackTime;
	private String comments;

}
