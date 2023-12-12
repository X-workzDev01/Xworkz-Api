package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	
	public CourseDto( String value) {
		
		this.course= value;
		this.branch= value;
		this.trainerName = value;
		this.batchType = value;
		this.batchTiming = value;
		this.startTime = value;
		this.offeredAs = value;
	}

}
