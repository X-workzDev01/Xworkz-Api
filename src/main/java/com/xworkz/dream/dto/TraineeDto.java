package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class TraineeDto {

	private Integer id;
	@JsonProperty("basicInfo")
	private BasicInfoDto basicInfo;
	@JsonProperty("educationInfo")
	private EducationInfoDto educationInfo;
	@JsonProperty("courseInfo")
	private CourseDto courseInfo;
	@JsonProperty("othersDto")
	private OthersDto othersDto ; 
	@JsonProperty("adminDto")
	private AuditDto adminDto;

	public Integer getId() {
		return id;
	}

	public TraineeDto(Integer id, BasicInfoDto basicInfo, EducationInfoDto educationInfo, CourseDto courseInfo,
			AuditDto adminDto) {
		super();
		this.id = id;
		this.basicInfo = basicInfo;
		this.educationInfo = educationInfo;
		this.courseInfo = courseInfo;
		this.adminDto = adminDto;
	}

}
