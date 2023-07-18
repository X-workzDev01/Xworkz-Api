package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FollowUpDto {
	
	
	private String email;
	private String name;
	private Long phoneNumber;
	private String status;
	private String comments;
	private String updatedBy;
	private String updatedOn;



 

}
