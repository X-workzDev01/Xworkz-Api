package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientHrDto {

	private Integer id;
	@JsonProperty("companyId")
	private Integer companyId;
	@JsonProperty("hrSpocName")
	private String hrSpocName;
	@JsonProperty("hrEmail")
	private String hrEmail;
	@JsonProperty("hrContactNumber")
	private Long hrContactNumber;
	@JsonProperty("designation")
	private String designation;
	@JsonProperty("status")
	private String status;
	@JsonProperty("adminDto")
	private AuditDto adminDto;

}
