package com.xworkz.dream.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//name
//contactNumber
//emailAddress
//qualification
//stream
//yearOfPassout


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicInfoDto {
	
	private String name;
	private Long contactNumber;
	private String email;

}
