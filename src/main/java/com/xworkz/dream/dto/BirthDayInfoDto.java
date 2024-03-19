package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BirthDayInfoDto {
	@JsonProperty("id")
	private Integer id;
	@JsonProperty("traineeEmail")
	private String traineeEmail;
	private String birthDayMailSent;
	private AuditDto auditDto;
	
}
