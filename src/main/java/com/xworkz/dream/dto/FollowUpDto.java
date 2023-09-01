package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class FollowUpDto {

	private Integer id;
	private BasicInfoDto basicInfo;
	private String registrationDate;
	private String joiningDate;
	private String courseName;
	private String currentlyFollowedBy;
	private String currentStatus;

	public Integer getId() {
		return id;
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

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(String joiningDate) {
		this.joiningDate = joiningDate;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCurrentlyFollowedBy() {
		return currentlyFollowedBy;
	}

	public void setCurrentlyFollowedBy(String currentlyFollowedBy) {
		this.currentlyFollowedBy = currentlyFollowedBy;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

}
