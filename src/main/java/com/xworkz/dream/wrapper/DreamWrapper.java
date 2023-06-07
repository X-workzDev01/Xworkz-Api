package com.xworkz.dream.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.xworkz.dream.dto.TraineeDto;

public class DreamWrapper {

	public static List<Object> dtoToList(TraineeDto dto) {
		List<Object> row = new ArrayList<>();
		row.add(dto.getBasicInfo().getTraineeName());
		row.add(dto.getBasicInfo().getEmail());
		row.add(dto.getBasicInfo().getContactNumber());
		row.add(dto.getEducationInfo().getQualification());
		row.add(dto.getEducationInfo().getStream());
		row.add(dto.getEducationInfo().getYearOfPassout());
		row.add(dto.getEducationInfo().getCollegeName());
		row.add(dto.getCourseInfo().getBatch());
		row.add(dto.getCourseInfo().getBranch());
		row.add(dto.getCourseInfo().getCourse());
		row.add(dto.getReferralInfo().getReferalName());
		row.add(dto.getReferralInfo().getReferalContactNumber());
		row.add(dto.getReferralInfo().getComments());

		return row;

	}

}
