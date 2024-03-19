package com.xworkz.dream.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.Sheets;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
public class PropertiesDto {

	@Value("${login.sheetId}")
	private String id;
	@Value("${sheets.appName}")
	private String applicationName;
	@Value("${sheets.credentialsPath}")
	private String credentialsPath;
	@Value("${sheets.clientInformationRange}")
	private String clientInformationRange;
	@Value("${sheets.clientInformationReadRange}")
	private String clientInformationReadRange;
	@Value("${login.sheetId}")
	public String sheetId;
	@Value("${sheets.clientEmailRange}")
	public String clientEmailRange;
	@Value("${sheets.clientContactNumberRange}")
	public String clientContactNumberRange;
	@Value("${sheets.clientWebsiteRange}")
	public String clientWebsiteRange;
	private Sheets sheetsService;
	@Value("${sheets.clientHrInformationRange}")
	private String clientHrInformationRange;
	@Value("${sheets.clientHrInformationReadRange}")
	private String clientHrInformationReadRange;
	@Value("${sheets.hrFollowUpInformationRange}")
	private String hrFollowUpInformationRange;
	@Value("${sheets.hrFollowUpInformationReadRange}")
	private String hrFollowUpInformationReadRange;
	@Value("${sheets.clientNameRange}")
	private String clientNameRange;
	@Value("${sheets.clientStartRow}")
	private String clientStartRow;
	@Value("${sheets.clientEndRow}")
	private String clientEndRow;
	@Value("${sheets.clientSheetName}")
	private String clientSheetName;
	@Value("${sheets.hrStartRow}")
	private String hrStartRow;
	@Value("${sheets.hrEndRow}")
	private String hrEndRow;
	@Value("${sheets.hrSheetName}")
	private String hrSheetName;
	@Value("${sheets.clientHrEmailRange}")
	private String clientHrEmailRange;
	@Value("${sheets.clientHrContactNumberRange}")
	private String clientHrContactNumberRange;
}
