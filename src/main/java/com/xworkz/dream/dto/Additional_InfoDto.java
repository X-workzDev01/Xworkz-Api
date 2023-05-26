package com.xworkz.dream.dto;

import com.xworkz.dream.constants.BatchInfo;

import lombok.AllArgsConstructor;
import lombok.Data;

//course
//batch
//xworkzBranch
//source
//collegeName
//referenceName
//referenceMobileNumber

@Data
@AllArgsConstructor
public class Additional_InfoDto {
	
	private String course;
	private String branch;
	private BatchInfo batch;
	
	
	

}
