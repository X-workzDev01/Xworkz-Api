package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xworkz.dream.constants.BatchInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//course
//batch
//xworkzBranch
//source
//collegeName
//referenceName
//referenceMobileNumber

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
	@JsonProperty("course")
	private String course;
	@JsonProperty("branch")
	private String branch;
	@JsonProperty("trainerName")
	private String trainerName;
	@JsonProperty("batchType")
	private String batchType;
	@JsonProperty("batchTiming")
	private String batchTiming;
	@JsonProperty("startTime")
	private String startTime;
	@JsonProperty("offeredAs")
	private String offeredAs;

}
