package com.xworkz.dream.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
@Component
public class SheetPropertyDto {
	
	@Value("${login.sheetId}")
	public String sheetId;
	@Value("${sheets.dateOfBirthDetailsRange}")
	private String dateOfBirthDetailsRange;
	@Value("${sheets.birthdayRange}")
	private String birthdayRange;
	@Value("${sheets.birthDayStartRow}")
	private String birthDayStartRow;
	@Value("${sheets.birthDayEndRow}")
	private String birthDayEndRow;
	@Value("${sheets.dateOfBirthDetailsSheetName}")
	private String dobSheetName;
	@Value("${sheets.rowStartRange}")
	private String rowStartRange;
	@Value("${sheets.rowEndRange}")
	private String rowEndRange;
	@Value("${sheets.traineeSheetName}")
	private String traineeSheetName;
	@Value("${sheets.birthDayEmailRange}")
	private String birthDayEmailRange;
	@Value("${sheets.batchDetails}")
	private String batchDetails;
	@Value("${sheets.batchDetailsRange}")
	private String batchDetailsRange;
	@Value("${sheets.batchIdRange}")
	private String batchIdRange;
	@Value("${sheets.batchDetailsCourseNameRange}")
	private String batchDetailsCourseNameRange;
	

}
