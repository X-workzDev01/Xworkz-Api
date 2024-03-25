package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BirthdayDetailsDto {

	private int id;
	private BasicInfoDto basicInfoDto;
	private String courseName;
	private String birthDayMailSent;

}