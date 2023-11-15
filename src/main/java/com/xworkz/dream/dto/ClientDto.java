package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class ClientDto {

	@JsonProperty("id")
	private int id;
	@JsonProperty("companyName")
	private String companyName;
	@JsonProperty("hrScop")
	private String hrScop;
	@JsonProperty("hrContactNumber")
	private Long hrContactNumber;
	@JsonProperty("hrMailId")
	private String hrMailId;
	@JsonProperty("companyLandLine")
	private Long companyLandLine;
	@JsonProperty("location")
	private String location;
	@JsonProperty("status")
	private String status;
	@JsonProperty("registrationDate")
	private String registrationDate;
	@JsonProperty("comments")
	private String comments;
	@JsonProperty("adminDto")
	private AdminDto adminDto;
}
