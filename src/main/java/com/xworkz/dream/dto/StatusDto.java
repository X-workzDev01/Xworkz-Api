package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusDto {

	@JsonProperty("id")
	private int id;
	@JsonProperty("basicInfo")
	private BasicInfoDto basicInfo;
	@JsonProperty("courseInfo")
	private CourseDto courseInfo;
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
	@JsonProperty("joiningDate")
	private String joiningDate;
}
