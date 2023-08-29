package com.xworkz.dream.dto;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusDto {
	
	@JsonProperty("id")
	private int id;
	@JsonProperty("basicInfo")
	private BasicInfoDto basicInfo;
	@JsonProperty("attemptedOn")
	private String attemptedOn;
	@JsonProperty("attemptedBy")
	private String attemptedBy;
	@JsonProperty("attemptStatus")
	private String attemptStatus;
	@JsonProperty("comments")
	private String comments;
	@JsonProperty("callDuration")
	private String callDuration;
	@JsonProperty("callBack")
	private String callBack;
	@JsonProperty("callBackTime")
	private String callBackTime;
}
