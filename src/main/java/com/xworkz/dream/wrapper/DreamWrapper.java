package com.xworkz.dream.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.xworkz.dream.dto.Student_InfoDto;

public class DreamWrapper {
	
	public static List<Object> dtoToList(Student_InfoDto dto){
		List<Object> row = new ArrayList<>();
		row.add(dto.getBasicInfo().getName());
		row.add(dto.getBasicInfo().getEmail());
		row.add(dto.getBasicInfo().getContactNumber());
		row.add(dto.getEducationInfoDto().getQualification());
		row.add(dto.getEducationInfoDto().getStream());
		row.add(dto.getEducationInfoDto().getYearOfPassout());
		row.add(dto.getEducationInfoDto().getCollegeName());
		row.add(dto.getAdditionalInfo().getBatch());
		row.add(dto.getAdditionalInfo().getBranch());
		row.add(dto.getAdditionalInfo().getCourse());
		row.add(dto.getReferalDetails().getReferalName());
		row.add(dto.getReferalDetails().getReferalContactNumber());
		row.add(dto.getReferalDetails().getComments());
		
		
		
		
		return row;
		
	}

}
