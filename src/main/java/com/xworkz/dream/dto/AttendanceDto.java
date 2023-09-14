package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {

	@JsonProperty("id")
	private int id;
	@JsonProperty("basicInfo")
	private BasicInfoDto basicInfo;
	@JsonProperty("courseInfo")
	private CourseDto courseInfo;
	@JsonProperty("preferredLocation")
	private String preferredLocation;
	@JsonProperty("preferredClassType")
	private String preferredClassType;
	@JsonProperty("attemptStatus")
	private String attemptStatus;

}
