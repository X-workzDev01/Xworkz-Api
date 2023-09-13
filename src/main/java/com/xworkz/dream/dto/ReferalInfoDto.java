package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReferalInfoDto {

	private String referalName;
	private Long referalContactNumber;
	private String comments;
	private String xworkzEmail;
	private String working;
	private String PreferredLocation;
	private String PreferredClassType;


}
