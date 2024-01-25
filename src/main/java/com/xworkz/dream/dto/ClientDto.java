package com.xworkz.dream.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Data
public class ClientDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	@JsonProperty("companyName")
	private String companyName;
	@JsonProperty("companyEmail")
	private String companyEmail;
	@JsonProperty("companyLandLineNumber")
	private Long companyLandLineNumber;
	@JsonProperty("companyWebsite")
	private String companyWebsite;
	@JsonProperty("companyLocation")
	private String companyLocation;
	@JsonProperty("companyFounder")
	private String companyFounder;
	@JsonProperty("sourceOfConnetion")
	private String sourceOfConnetion;
	@JsonProperty("companyType")
	private String companyType;
	@JsonProperty("companyAddress")
	private String companyAddress;
	@JsonProperty("status")
	private String status;
	@JsonProperty("adminDto")
	private AuditDto adminDto;

}
