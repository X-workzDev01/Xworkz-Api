package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *This will get the Absentees data frontend
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsenteesDto {
	
	@JsonProperty("id")
	private int id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("reason")
	private String reason;
	
	
	
	
	

}
