package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDetails {
	private Integer id;
	private String courseName;
	private String trainerName;
	private String startTime;
	private String batchType;
	private String timing;
	private String branch;
	private String status;

}
