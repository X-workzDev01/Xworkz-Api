package com.xworkz.dream.wrapper;

import java.util.List;

import com.xworkz.dream.dto.HrFollowUpDto;

public interface HrFollowUpWrapper {
	
	abstract void settingNaValues(HrFollowUpDto dto);
	abstract HrFollowUpDto listToHrFollowUpDto(List<Object> row);

}
