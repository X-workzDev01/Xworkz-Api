package com.xworkz.dream.dto;

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
public class FeesDto {

	private String name;
	private String email;
	private String batchStartDate;
	private String reminderDate;
	private String lastFeesPaidDate;
	private String transectionId;
	private String totalAmount;
	private String paidAmount;
	private String balance;
	private String status;
	private String mailSendStatus;
	private String comments;

}
