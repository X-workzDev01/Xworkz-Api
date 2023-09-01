package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BasicInfoDto {

	private String traineeName;
	private String email;
	private Long contactNumber;
	private String dateOfBirth;
	
}
