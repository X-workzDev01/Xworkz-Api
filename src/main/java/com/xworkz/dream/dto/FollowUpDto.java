package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FollowUpDto {

	private Integer id;
	private BasicInfoDto basicInfo;
	private String registrationDate;
	private String joiningDate;
	private String courseName;
	private String currentlyFollowedBy;
	private String currentStatus;

}
