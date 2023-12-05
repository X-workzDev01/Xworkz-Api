package com.xworkz.dream.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 

public class AuditDto {

	private String createdBy;
	private String createdOn;
	private String updatedBy;
	private String updatedOn;

}
