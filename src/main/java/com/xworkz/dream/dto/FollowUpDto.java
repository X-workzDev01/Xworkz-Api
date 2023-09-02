package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("basicInfo")
	private BasicInfoDto basicInfo;
	@JsonProperty("registrationDate")
	private String registrationDate;
	@JsonProperty("joiningDate")
	private String joiningDate;
	@JsonProperty("courseName")
	private String courseName;
	@JsonProperty("currentlyFollowedBy")
	private String currentlyFollowedBy;
	@JsonProperty("currentStatus")
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
