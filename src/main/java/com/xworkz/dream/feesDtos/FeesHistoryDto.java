package com.xworkz.dream.feesDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FeesHistoryDto {
	private Integer id;
	private String email;
	private String lastFeesPaidDate;
	private String transectionId;
	private String paidAmount;
	private String paymentMode;
	private String paidTo;
	private String feesfollowupDate;
	private String followupCallbackDate;

}
