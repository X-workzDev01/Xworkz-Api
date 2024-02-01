package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CSR {

	@JsonProperty("usnNumber")
	private String usnNumber;
	@JsonProperty("alternateContactNumber")
	private Long alternateContactNumber;
	@JsonProperty("uniqueId")
	private String uniqueId;
	private String csrFlag;
	private String activeFlag;

}
