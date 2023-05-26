package com.xworkz.dream.dto;

import com.xworkz.dream.constants.Interest;

import lombok.Data;

//enum - Interested
//Official Status
//joinedWhatsapp
//Comments

@Data
public class StatusDto {
	
	private Interest interestInfo;
	private boolean joinedWhatsapp;
	private Status status;
	private String otherComments;
	
	

}
