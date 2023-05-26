package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Student_InfoDto {
	
	private BasicInfoDto basicInfo;
	private EducationInfoDto educationInfoDto;
	private Additional_InfoDto additionalInfo;
	private ReferalDetailsDto referalDetails;
	

}
