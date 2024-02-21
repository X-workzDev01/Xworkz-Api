package com.xworkz.dream.service;

import java.util.List;

import com.xworkz.dream.dto.HrFollowUpDto;

public interface HrFollowUpService {
	
	String saveHrFollowUpDetails(HrFollowUpDto dto);
	List<HrFollowUpDto> getHrFollowUpDetailsBy(int hrId);
	List<HrFollowUpDto> getFollowUpDetails(Integer companyId);
	
}
