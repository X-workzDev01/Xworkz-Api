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

	@JsonProperty("attendanceId")
	private Integer attendanceId;
	@JsonProperty("id")
	private Integer id;
	@JsonProperty("courseInfo")
	private CourseDto courseInfo;
	@JsonProperty("attemptStatus")
	private String attemptStatus;
	@JsonProperty("totalAbsent")
	private Integer totalAbsent;
	@JsonProperty("absentDate")
	private String absentDate;
	@JsonProperty("reason")
	private String reason;
	
	
	



}
