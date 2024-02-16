package com.xworkz.dream.dto;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PropertiesDto {

	@Value("${login.sheetId}")
	private String id;
	
}
