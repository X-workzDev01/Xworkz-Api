package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchAttendanceDto {
	
	@JsonProperty("id")
	private Integer id;
	@JsonProperty("batchName")
	private String batchName;
	@JsonProperty("trainerName")
	private String trainerName;
	@JsonProperty("presentDate")
	private String presentDate;

}
