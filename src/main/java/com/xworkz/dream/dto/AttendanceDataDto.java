package com.xworkz.dream.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceDataDto {
	
	private List<AttendanceDto> attendanceData;
	private Integer size;

}