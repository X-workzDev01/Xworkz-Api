package com.xworkz.dream.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.xworkz.dream.dto.BasicInfoDto;
import com.xworkz.dream.dto.CourseDto;
import com.xworkz.dream.dto.EducationInfoDto;
import com.xworkz.dream.dto.ReferalInfoDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.User;

@Component
public class DreamWrapper {

	public  List<Object> dtoToList(TraineeDto dto) {
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
	
	public TraineeDto listToDto(List<Object> row) {
	    TraineeDto traineeDto = new TraineeDto(new BasicInfoDto(), new EducationInfoDto(), new CourseDto(), new ReferalInfoDto());

	    // Assuming the list follows this order: traineeName, email, contactNumber, qualification, stream,
	    // yearOfPassout, collegeName, batch, branch, course, referalName, referalContactNumber, comments

	    if (row.get(0) != null && !row.get(0).toString().isEmpty()) {
	        traineeDto.getBasicInfo().setTraineeName((String) row.get(0));
	    }
	    if (row.get(1) != null && !row.get(1).toString().isEmpty()) {
	        traineeDto.getBasicInfo().setEmail((String) row.get(1));
	    }
	    if (row.get(2) != null && !row.get(2).toString().isEmpty()) {
	        traineeDto.getBasicInfo().setContactNumber(Long.parseLong(row.get(2).toString()));
	    }
	    if (row.get(3) != null && !row.get(3).toString().isEmpty()) {
	        traineeDto.getEducationInfo().setQualification((String) row.get(3));
	    }
	    if (row.get(4) != null && !row.get(4).toString().isEmpty()) {
	        traineeDto.getEducationInfo().setStream((String) row.get(4));
	    }
	    if (row.get(5) != null && !row.get(5).toString().isEmpty()) {
	        traineeDto.getEducationInfo().setYearOfPassout((String) row.get(5));
	    }
	    if (row.get(6) != null && !row.get(6).toString().isEmpty()) {
	        traineeDto.getEducationInfo().setCollegeName((String) row.get(6));
	    }
	    if (row.get(7) != null && !row.get(7).toString().isEmpty()) {
	        traineeDto.getCourseInfo().setBatch((String) row.get(7));
	    }
	    if (row.get(8) != null && !row.get(8).toString().isEmpty()) {
	        traineeDto.getCourseInfo().setBranch((String) row.get(8));
	    }
	    if (row.get(9) != null && !row.get(9).toString().isEmpty()) {
	        traineeDto.getCourseInfo().setCourse((String) row.get(9));
	    }
	    if (row.get(10) != null && !row.get(10).toString().isEmpty()) {
	        traineeDto.getReferralInfo().setReferalName((String) row.get(10));
	    }
	    if (row.get(11) != null && !row.get(11).toString().isEmpty()) {
	        traineeDto.getReferralInfo().setReferalContactNumber(Long.parseLong(row.get(11).toString()));
	    }
	    if (row.get(12) != null && !row.get(12).toString().isEmpty()) {
	        traineeDto.getReferralInfo().setComments((String) row.get(12));
	    }

	    return traineeDto;
	}
	
	
	
	public static List<Object> userToList(User user) {
		List<Object> row = new ArrayList<>();
		row.add(user.getEmail());
		row.add(user.getLoginTime());
		return row;
		
	}
	
	

}
