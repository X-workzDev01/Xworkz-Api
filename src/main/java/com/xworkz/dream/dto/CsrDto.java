package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsrDto {

	@JsonProperty("basicInfo")
	private BasicInfoDto basicInfo;
	@JsonProperty("educationInfo")
	private EducationInfoDto educationInfo;
	@JsonProperty("usnNumber")
	private Integer usnNumber;
	@JsonProperty("alternateContactNumber")
	private Long alternateContactNumber;
	@JsonProperty("adminDto")
	private AuditDto adminDto;

}
