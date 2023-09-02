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

	@JsonProperty("referralInfo")
	private ReferalInfoDto referralInfo;

//	private AdminDto adminDto;

	public Integer getId() {
		return id;
	}

	public TraineeDto(Integer id, BasicInfoDto basicInfo, EducationInfoDto educationInfo, CourseDto courseInfo) {
		super();
		this.id = id;
		this.basicInfo = basicInfo;
		this.educationInfo = educationInfo;
		this.courseInfo = courseInfo;

	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BasicInfoDto getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(BasicInfoDto basicInfo) {
		this.basicInfo = basicInfo;
	}

	public EducationInfoDto getEducationInfo() {
		return educationInfo;
	}

	public void setEducationInfo(EducationInfoDto educationInfo) {
		this.educationInfo = educationInfo;
	}

	public CourseDto getCourseInfo() {
		return courseInfo;
	}

	public void setCourseInfo(CourseDto courseInfo) {
		this.courseInfo = courseInfo;
	}

	public ReferalInfoDto getReferralInfo() {
		return referralInfo;
	}

	public void setReferralInfo(ReferalInfoDto referralInfo) {
		this.referralInfo = referralInfo;
	}

}
