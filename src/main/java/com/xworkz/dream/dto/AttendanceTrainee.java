package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
*This class will give the trainee present in Attendance Sheet
*/


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceTrainee {
	
	@JsonProperty("id")
	private int id;
	@JsonProperty("name")
	private String name;

}
