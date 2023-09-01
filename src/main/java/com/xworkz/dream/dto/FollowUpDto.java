package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FollowUpDto {

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("basicInfo")
	private BasicInfoDto basicInfo;
	@JsonProperty("registrationDate")
	private String registrationDate;
	@JsonProperty("joiningDate")
	private String joiningDate;
	@JsonProperty("courseName")
	private String courseName;
	@JsonProperty("currentlyFollowedBy")
	private String currentlyFollowedBy;
	@JsonProperty("currentStatus")
	private String currentStatus;
	

}
