package com.xworkz.dream.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SheetsDto {

	private List<TraineeDto> sheetsData;
	private int size;

}
