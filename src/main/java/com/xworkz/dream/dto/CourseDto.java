package com.xworkz.dream.dto;

import com.xworkz.dream.constants.BatchInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//course
//batch
//xworkzBranch
//source
//collegeName
//referenceName
//referenceMobileNumber

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

	private String course;
	private String branch;
	private String batch;
	private String offeredAs;

}
