package com.xworkz.dream.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BatchDetailsDto {

	private int id;
	private String courseName;
	private String trainerName;
	private String startDate;
	private String batchType;
	private String startTime;
	private String branchName;
	private String batchStatus;

}
