package com.xworkz.dream.feesDtos;

import com.xworkz.dream.dto.AdminDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public   class FeesDto {
	private int id;
	private String name;
	private FeesHistoryDto feesHistoryDto;
	private String reminderDate;
	private Long totalAmount;
	private Long balance;
	private Long feeConcession;
	private String feesStatus;
	private String mailSendStatus;
	private String comments;
	private AdminDto admin;
	private String softFlag;
	private String courseName;

	public FeesDto(FeesHistoryDto feesHistoryDto, AdminDto admin) {
		this.feesHistoryDto = feesHistoryDto;
		this.admin = admin;
	}

}
