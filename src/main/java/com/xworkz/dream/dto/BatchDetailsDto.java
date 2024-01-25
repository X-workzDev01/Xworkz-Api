package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BatchDetailsDto {

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("courseName")
	private String courseName;
	@JsonProperty("trainerName")
	private String trainerName;
	@JsonProperty("startDate")
	private String startDate;
	@JsonProperty("batchType")
	private String batchType;
	@JsonProperty("startTime")
	private String startTime;
	@JsonProperty("branchName")
	private String branchName;
	@JsonProperty("batchStatus")
	private String batchStatus;
	@JsonProperty("whatsAppLink")
	private String whatsAppLink;
	@JsonProperty("totalAmount")
	private Long totalAmount;
	@JsonProperty("totalClass")
	private Integer totalClass;
	

}
