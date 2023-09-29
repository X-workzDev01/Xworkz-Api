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
public class OthersDto {

	private String referalName;
	private Long referalContactNumber;
	private String comments;
	private String xworkzEmail;
	private String working;
	private String PreferredLocation;
	private String PreferredClassType;
	private String sendWhatsAppLink;
	
	public OthersDto(String value) {
		this.referalName=value;
		this.referalContactNumber=0L;
		this.comments = value;
		this.xworkzEmail = value;
		this.working = value;
		this.PreferredLocation = value;
		this.PreferredClassType = value;
		this.sendWhatsAppLink = value;
	}


}
