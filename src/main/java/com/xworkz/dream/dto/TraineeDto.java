package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeDto {
	
	private Integer id;
	@JsonProperty("basicInfo")
	private BasicInfoDto basicInfo;
	@JsonProperty("educationInfo")
	private EducationInfoDto educationInfo;
	@JsonProperty("courseInfo")
	private CourseDto courseInfo;
	@JsonProperty("referralInfo")
	private ReferalInfoDto referralInfo;
	

}
