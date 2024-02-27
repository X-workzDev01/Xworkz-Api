package com.xworkz.dream.feesDtos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FeesFinalDto {
	@Value("${sheets.feesFollowUpUpdateRange}")
	private String feesUpdateRange;
	@Value("${sheets.feesUpdateStartRange}")
	private String feesUpdateStartRange;
	@Value("${sheets.feesUpdateEndRange}")
	private String feesUpdateEndRange;
	@Value("${sheets.feesRegister}")
	private String feesRegisterRange;
	@Value("${sheets.feesEmailRange}")
	private String feesEmailRange;
	@Value("${login.sheetId}")
	private String id;
	@Value("${sheets.getFeesDetiles}")
	private String getFeesDetilesRange;
	@Value("${sheets.getFeesDetilesfollowupRange}")
	private String getFeesDetilesfollowupRange;
	@Value("${sheets.feesFollowUpEmailRange}")
	private String feesFollowUpEmailRange;

}
