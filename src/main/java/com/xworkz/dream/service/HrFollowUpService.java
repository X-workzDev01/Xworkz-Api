package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import com.xworkz.dream.dto.HrFollowUpDto;

public interface HrFollowUpService {
	
	abstract String saveHrFollowUpDetails(HrFollowUpDto dto) throws IllegalAccessException, IOException;
	List<HrFollowUpDto> getHrFollowUpDetailsBy(int hrId) throws IOException;
	
}
