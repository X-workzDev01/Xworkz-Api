package com.xworkz.dream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadCsrDto {

	private String uniqueId;
	private String studentName;
	private Long contactNumber;
	private Long whatsAppNumber;
	private String emailId;
	private String usnNumber;
	private String qualification;
	private String stream;
	private String collegeName;

}