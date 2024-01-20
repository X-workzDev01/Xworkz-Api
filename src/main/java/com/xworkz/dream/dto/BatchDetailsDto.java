package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchDetailsDto {

	private Integer id;
	private String courseName;
	private String trainerName;
	private String startDate;
	private String batchType;
	private String startTime;
	private String branchName;
	private String batchStatus;
	private String whatsAppLink;
	private Long totalAmount;
	private Integer totalClass;

}
