package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicInfoDto {

	private String traineeName;
	private String email;
	private Long contactNumber;
	private String dateOfBirth;
}
