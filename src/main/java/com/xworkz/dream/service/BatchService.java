package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;

public interface BatchService {
	public List<TraineeDto> getTraineeDetailsByCourse(String spreadsheetId, String courseName)
			throws IOException;

	public List<FollowUpDto> getTraineeDetailsByCourseInFollowUp(String spreadsheetId,
			String courseName) throws IOException;

	public List<FollowUpDto> traineeDetailsByCourseAndStatusInFollowUp(String spreadsheetId,
			String courseName, String status) throws IOException;

	public List<FollowUpDto> getGroupStatus(String spreadsheetId, String status) throws IOException;

	public List<List<Object>> getList(String spreadsheetId) throws IOException;
	
}
