package com.xworkz.dream.dto;


import lombok.AllArgsConstructor;
import lombok.Data;


//name
//contactNumber
//emailAddress
//qualification
//stream
//yearOfPassout


@Data
@AllArgsConstructor
public class BasicInfoDto {
	
	private String name;
	private Long contactNumber;
	private String email;

}
