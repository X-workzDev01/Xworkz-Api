package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
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
	private String whatsAppLink;

}
