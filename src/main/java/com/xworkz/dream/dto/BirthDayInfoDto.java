package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BirthDayInfoDto {
	@JsonProperty("id")
	private int id;
	@JsonProperty("basicInfo")
	private BasicInfoDto dto;
	private String birthDayMailSent;
	private AuditDto auditDto;	
}
