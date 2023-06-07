package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferalInfoDto {

	private String referalName;
	private Long referalContactNumber;
	private String comments;

}
