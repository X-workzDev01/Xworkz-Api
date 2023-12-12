package com.xworkz.dream.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SheetsDto {
	
	
	private List<TraineeDto> sheetsData;
	private int size;
	
	
	

}
