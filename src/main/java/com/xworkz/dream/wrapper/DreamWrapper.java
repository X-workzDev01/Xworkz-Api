package com.xworkz.dream.wrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.xworkz.dream.dto.AdminDto;
import com.xworkz.dream.dto.BasicInfoDto;
import com.xworkz.dream.dto.CourseDto;
import com.xworkz.dream.dto.EducationInfoDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.ReferalInfoDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.User;

@Component
public class DreamWrapper {

//	public  List<Object> dtoToList(TraineeDto dto) {
//		List<Object> row = new ArrayList<>();
//		row.add(dto.getId());
//		row.add(dto.getBasicInfo().getTraineeName());
//		row.add(dto.getBasicInfo().getEmail());
//		row.add(dto.getBasicInfo().getContactNumber());
//		row.add(dto.getEducationInfo().getQualification());
//		row.add(dto.getEducationInfo().getStream());
//		row.add(dto.getEducationInfo().getYearOfPassout());
//		row.add(dto.getEducationInfo().getCollegeName());
//		row.add(dto.getCourseInfo().getBatch());
//		row.add(dto.getCourseInfo().getBranch());
//		row.add(dto.getCourseInfo().getCourse());
//		row.add(dto.getReferralInfo().getReferalName());
//		row.add(dto.getReferralInfo().getReferalContactNumber());
//		row.add(dto.getReferralInfo().getComments());
//
//		return row;
//
//	}
	
	public TraineeDto listToDto(List<Object> row) {
	    TraineeDto traineeDto = new TraineeDto(0 ,new BasicInfoDto(), new EducationInfoDto(), new CourseDto(), new ReferalInfoDto());

	    // Assuming the list follows this order: id ,traineeName, email, contactNumber, qualification, stream,
	    // yearOfPassout, collegeName, batch, branch, course, referalName, referalContactNumber, comments
	    // if there any changes in the table, please make sure the right changes are done here also
	    
	    int rowSize = row.size();

	    if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
	        traineeDto.setId(Integer.valueOf(row.get(0).toString()));
	    }

	    if (rowSize > 1 && row.get(1) != null && !row.get(1).toString().isEmpty()) {
	        traineeDto.getBasicInfo().setTraineeName((String) row.get(1));
	    }

	    if (rowSize > 2 && row.get(2) != null && !row.get(2).toString().isEmpty()) {
	        traineeDto.getBasicInfo().setEmail((String) row.get(2));
	    }

	    if (rowSize > 3 && row.get(3) != null && !row.get(3).toString().isEmpty()) {
	        traineeDto.getBasicInfo().setContactNumber(Long.parseLong(row.get(3).toString()));
	    }

	    if (rowSize > 4 && row.get(4) != null && !row.get(4).toString().isEmpty()) {
	        traineeDto.getEducationInfo().setQualification((String) row.get(4));
	    }

	    if (rowSize > 5 && row.get(5) != null && !row.get(5).toString().isEmpty()) {
	        traineeDto.getEducationInfo().setStream((String) row.get(5));
	    }

	    if (rowSize > 6 && row.get(6) != null && !row.get(6).toString().isEmpty()) {
	        traineeDto.getEducationInfo().setYearOfPassout((String) row.get(6));
	    }

	    if (rowSize > 7 && row.get(7) != null && !row.get(7).toString().isEmpty()) {
	        traineeDto.getEducationInfo().setCollegeName((String) row.get(7));
	    }

	    if (rowSize > 8 && row.get(8) != null && !row.get(8).toString().isEmpty()) {
	        traineeDto.getCourseInfo().setBatch((String) row.get(8));
	    }

	    if (rowSize > 9 && row.get(9) != null && !row.get(9).toString().isEmpty()) {
	        traineeDto.getCourseInfo().setBranch((String) row.get(9));
	    }

	    if (rowSize > 10 && row.get(10) != null && !row.get(10).toString().isEmpty()) {
	        traineeDto.getCourseInfo().setCourse((String) row.get(10));
	    }

	    if (rowSize > 11 && row.get(11) != null && !row.get(11).toString().isEmpty()) {
	        traineeDto.getReferralInfo().setReferalName((String) row.get(11));
	    }

	    if (rowSize > 12 && row.get(12) != null && !row.get(12).toString().isEmpty()) {
	        traineeDto.getReferralInfo().setReferalContactNumber(Long.parseLong(row.get(12).toString()));
	    }

	    if (rowSize > 13 && row.get(13) != null && !row.get(13).toString().isEmpty()) {
	        traineeDto.getReferralInfo().setComments((String) row.get(13));
	    }

	    return traineeDto;
	}

	
	public static List<Object> userToList(User user) {
		List<Object> row = new ArrayList<>();
		row.add(user.getEmail());
		row.add(user.getLoginTime());
		return row;
		
	}
	
//	public List<Object> followUpToList(FollowUpDto dto){
//		
//		List<Object> row = new ArrayList<>();
//		row.add(dto.getId());
//		row.add(dto.getBasicInfo().getTraineeName());
//		row.add(dto.getBasicInfo().getEmail());
//		row.add(dto.getBasicInfo().getContactNumber());
//		row.add(dto.getRegistrationDate());
//		row.add(dto.getCourseName());
//		row.add(dto.getJoiningDate());
//		row.add(dto.getCurrentlyFollowedBy());
//		row.add(dto.getCurrentStatus());
//		
//		return row;
//		
//	}
	public List<Object> extractDtoDetails(Object dto) throws IllegalAccessException {
        List<Object> detailsList = new ArrayList<>();

        // Get all fields of the DTO class, including inherited fields
        Class<?> dtoClass = dto.getClass();
        Field[] fields = dtoClass.getDeclaredFields();

        for (Field field : fields) {
            // Make private fields accessible
            field.setAccessible(true);

            // Extract the value of the field from the DTO object
            Object fieldValue = field.get(dto);

            if (fieldValue != null && !field.getType().isPrimitive() && !field.getType().getName().startsWith("java")) {
                // Handle association with another DTO
                List<Object> subDtoDetails = extractDtoDetails(fieldValue);
                detailsList.addAll(subDtoDetails);
            } else {
                // Add the value to the list
                detailsList.add(fieldValue);
            }
        }

        return detailsList;
    }



	

}
