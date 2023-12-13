package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class FollowUpDto {

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("basicInfo")
	private BasicInfoDto basicInfo;
	@JsonProperty("registrationDate")
	private String registrationDate;
	@JsonProperty("joiningDate")
	private String joiningDate;
	@JsonProperty("courseName")
	private String courseName;
	@JsonProperty("currentlyFollowedBy")
	private String currentlyFollowedBy;
	@JsonProperty("currentStatus")
	private String currentStatus;
	private String callback;
	@JsonProperty("adminDto")
	private AuditDto adminDto;
	String flag;

}
