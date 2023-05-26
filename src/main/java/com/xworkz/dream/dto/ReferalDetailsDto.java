package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReferalDetailsDto {
	
	private String referalName;
	private Long referalContactNumber;
	private String comments; 

}
