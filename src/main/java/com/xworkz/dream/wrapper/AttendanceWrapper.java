package com.xworkz.dream.wrapper;

import java.util.List;

import com.xworkz.dream.dto.AttendanceSheetDto;
import com.xworkz.dream.dto.AttendanceTrainee;

public interface AttendanceWrapper {
	
	public AttendanceSheetDto listToAttendanceDto(List<Object> list);
	
	

}
