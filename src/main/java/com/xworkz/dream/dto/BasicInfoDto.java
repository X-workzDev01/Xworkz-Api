package com.xworkz.dream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	@JsonProperty("traineeName")
	private String traineeName;
	@JsonProperty("email")
	private String email;
	@JsonProperty("contactNumber")
	private Long contactNumber;
	@JsonProperty("dateOfBirth")
	private String dateOfBirth;


}
