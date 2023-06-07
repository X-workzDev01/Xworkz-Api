package com.xworkz.dream.dto;

import com.xworkz.dream.constants.BatchInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//course
//batch
//xworkzBranch
//source
//collegeName
//referenceName
//referenceMobileNumber

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

	private String course;
	private String branch;
	private String batch;

}
