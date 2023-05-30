package com.xworkz.dream.dto;

import com.xworkz.dream.constants.Interest;

import lombok.Data;
import lombok.NoArgsConstructor;

//enum - Interested
//Official Status
//joinedWhatsapp
//Comments

@Data
@NoArgsConstructor
public class StatusDto {
	
	private Interest interestInfo;
	private boolean joinedWhatsapp;
	private Status status;
	private String otherComments;
	
	

}
