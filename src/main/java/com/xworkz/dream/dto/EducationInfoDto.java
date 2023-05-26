package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EducationInfoDto {
	
	private String qualification;
	private String stream;
	private Integer yearOfPassout;
	private String collegeName;

}
