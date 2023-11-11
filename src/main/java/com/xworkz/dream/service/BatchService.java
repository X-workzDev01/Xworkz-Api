package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import com.xworkz.dream.dto.FollowUpDataDto;
import com.xworkz.dream.dto.TraineeDto;

public interface BatchService {
	public List<TraineeDto> getTraineeDetailsByCourse(String spreadsheetId, String courseName)
			throws IOException;

	public FollowUpDataDto getTraineeDetailsByCourseInFollowUp(String spreadsheetId,
			String courseName,int startingIndex,int maxRows) throws IOException;
}
