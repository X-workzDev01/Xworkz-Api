package com.xworkz.dream.dto;

import java.util.List;

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
public class BirthdayDataDto {
	
	private List<BirthdayDetailsDto> listofBirthdays;
	private int size;

}
