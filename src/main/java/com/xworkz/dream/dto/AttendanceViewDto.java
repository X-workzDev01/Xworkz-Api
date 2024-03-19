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
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceViewDto {
	
	@JsonProperty("id")
	private Integer id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("email")
	private String email;
	@JsonProperty("courseName")
	private String courseName;
	@JsonProperty("totalAbsent")
	private Integer totalAbsent;

}
