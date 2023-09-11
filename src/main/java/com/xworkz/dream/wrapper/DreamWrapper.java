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
import com.xworkz.dream.dto.SuggestionDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.User;

@Component
public class DreamWrapper {

	public List<Object> dtoToList(TraineeDto dto) {
		List<Object> row = new ArrayList<>();
		row.add(dto.getId());
		row.add(dto.getBasicInfo().getTraineeName());
		row.add(dto.getBasicInfo().getEmail());
		row.add(dto.getBasicInfo().getContactNumber());
		row.add(dto.getEducationInfo().getQualification());
		row.add(dto.getEducationInfo().getStream());
		row.add(dto.getEducationInfo().getYearOfPassout());
		row.add(dto.getEducationInfo().getCollegeName());
		row.add(dto.getCourseInfo().getTrainerName());
		row.add(dto.getCourseInfo().getBranch());
		row.add(dto.getCourseInfo().getCourse());
		row.add(dto.getReferralInfo().getReferalName());
		row.add(dto.getReferralInfo().getReferalContactNumber());
		row.add(dto.getReferralInfo().getComments());

		return row;

	}

	public SuggestionDto listToSuggestionDTO(List<Object> row) {
		SuggestionDto suggestionDto = new SuggestionDto();
		int rowSize = row.size();
		if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
			suggestionDto.setName((String) row.get(0));
		}
		if (rowSize > 1 && row.get(1) != null && !row.get(1).toString().isEmpty()) {
			suggestionDto.setEmail((String) row.get(1));
		}

		return suggestionDto;
	}

	public FollowUpDto listToFollowUpDTO(List<Object> row) {
		FollowUpDto followUpDto = new FollowUpDto(0, new BasicInfoDto(), null, null, null, null, null);
		int rowSize = row.size();
		if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
			followUpDto.setId(Integer.valueOf(row.get(0).toString()));
		}
		if (rowSize > 1 && row.get(1) != null && !row.get(1).toString().isEmpty()) {
			followUpDto.getBasicInfo().setTraineeName((String) row.get(1));
		}
		if (rowSize > 2 && row.get(2) != null && !row.get(2).toString().isEmpty()) {
			followUpDto.getBasicInfo().setEmail((String) row.get(2));
		}
		if (rowSize > 3 && row.get(3) != null && !row.get(3).toString().isEmpty()) {
			followUpDto.getBasicInfo().setContactNumber(Long.parseLong(row.get(3).toString()));
		}
		if (rowSize > 4 && row.get(4) != null && !row.get(4).toString().isEmpty()) {
			followUpDto.setRegistrationDate((String) row.get(4));
		}
		if (rowSize > 5 && row.get(5) != null && !row.get(5).toString().isEmpty()) {
			followUpDto.setJoiningDate((String) row.get(5));
		}
		if (rowSize > 6 && row.get(6) != null && !row.get(6).toString().isEmpty()) {
			followUpDto.setCourseName((String) row.get(6));
		}
		if (rowSize > 7 && row.get(7) != null && !row.get(7).toString().isEmpty()) {
			followUpDto.setCurrentlyFollowedBy((String) row.get(7));
		}
		if (rowSize > 8 && row.get(8) != null && !row.get(8).toString().isEmpty()) {
			followUpDto.setCurrentStatus((String) row.get(8));
		}

		return followUpDto;
	}

	public StatusDto listToStatusDto(List<Object> rows) {
		StatusDto statusDto = new StatusDto(0, new BasicInfoDto(), null, null, null, null, null, null, null);

		int rowSize = rows.size();
		if (rowSize > 0 && rows.get(0) != null && !rows.get(0).toString().isEmpty()) {
			statusDto.setId(Integer.valueOf(rows.get(0).toString()));
		}
		if (rowSize > 1 && rows.get(1) != null && !rows.get(1).toString().isEmpty()) {
			statusDto.getBasicInfo().setTraineeName((String) rows.get(1));
		}
		if (rowSize > 2 && rows.get(2) != null && !rows.get(2).toString().isEmpty()) {
			statusDto.getBasicInfo().setEmail((String) rows.get(2));
		}
		if (rowSize > 3 && rows.get(3) != null && !rows.get(3).toString().isEmpty()) {
			statusDto.setAttemptedOn((String) rows.get(3));
		}
		if (rowSize > 4 && rows.get(4) != null && !rows.get(4).toString().isEmpty()) {
			statusDto.setAttemptedBy((String) rows.get(4));
		}
		if (rowSize > 5 && rows.get(5) != null && !rows.get(5).toString().isEmpty()) {
			statusDto.setAttemptStatus((String) rows.get(5));
		}
		if (rowSize > 6 && rows.get(6) != null && !rows.get(6).toString().isEmpty()) {
			statusDto.setComments((String) rows.get(6));
		}
		if (rowSize > 7 && rows.get(7) != null && !rows.get(7).toString().isEmpty()) {
			statusDto.setCallDuration((String) rows.get(7));
		}
		if (rowSize > 8 && rows.get(8) != null && !rows.get(8).toString().isEmpty()) {
			statusDto.setCallBack((String) rows.get(8));
		}
		if (rowSize > 9 && rows.get(9) != null && !rows.get(9).toString().isEmpty()) {
			statusDto.setCallBackTime((String) rows.get(9));
		}

		return statusDto;
	}

	public TraineeDto listToDto(List<Object> row) {
		TraineeDto traineeDto = new TraineeDto(0, new BasicInfoDto(), new EducationInfoDto(), new CourseDto(),
				new ReferalInfoDto(), new AdminDto());

		// Assuming the list follows this order: id ,traineeName, email, contactNumber,
		// qualification, stream,
		// yearOfPassout, collegeName, batch, branch, course, referalName,
		// referalContactNumber, comments
		// if there any changes in the table, please make sure the right changes are
		// done here also

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
			traineeDto.getBasicInfo().setDateOfBirth((String) row.get(4));
		}

		if (rowSize > 5 && row.get(5) != null && !row.get(5).toString().isEmpty()) {
			traineeDto.getEducationInfo().setQualification((String) row.get(5));
		}

		if (rowSize > 6 && row.get(6) != null && !row.get(6).toString().isEmpty()) {
			traineeDto.getEducationInfo().setStream((String) row.get(6));
		}

		if (rowSize > 7 && row.get(7) != null && !row.get(7).toString().isEmpty()) {
			traineeDto.getEducationInfo().setYearOfPassout((String) row.get(7));
		}

		if (rowSize > 8 && row.get(8) != null && !row.get(8).toString().isEmpty()) {
			traineeDto.getEducationInfo().setCollegeName((String) row.get(8));
		}

		if (rowSize > 9 && row.get(9) != null && !row.get(9).toString().isEmpty()) {
			traineeDto.getCourseInfo().setCourse((String) row.get(9));
		}

		if (rowSize > 10 && row.get(10) != null && !row.get(10).toString().isEmpty()) {
			traineeDto.getCourseInfo().setBranch((String) row.get(10));
		}

		if (rowSize > 11 && row.get(11) != null && !row.get(11).toString().isEmpty()) {
			traineeDto.getCourseInfo().setTrainerName((String) row.get(11));
		}
		if (rowSize > 12 && row.get(12) != null && !row.get(12).toString().isEmpty()) {
			traineeDto.getCourseInfo().setBatchType((String) row.get(12));
		}
		if (rowSize > 13 && row.get(13) != null && !row.get(13).toString().isEmpty()) {
			traineeDto.getCourseInfo().setBatchTiming((String) row.get(13));
		}
		if (rowSize > 14 && row.get(14) != null && !row.get(14).toString().isEmpty()) {
			traineeDto.getCourseInfo().setStartTime((String) row.get(14));
		}

		if (rowSize > 15 && row.get(15) != null && !row.get(15).toString().isEmpty()) {
			traineeDto.getCourseInfo().setOfferedAs((String) (row.get(15).toString()));
		}

		if (rowSize > 16 && row.get(16) != null && !row.get(16).toString().isEmpty()) {
			traineeDto.getReferralInfo().setReferalName((String) row.get(16));
		}
		if (rowSize > 17 && row.get(17) != null && !row.get(17).toString().isEmpty()) {
			Long referalContactNumber = Long.parseLong(row.get(17).toString());
			traineeDto.getReferralInfo().setReferalContactNumber(referalContactNumber);
		}
		if (rowSize > 18 && row.get(18) != null && !row.get(18).toString().isEmpty()) {
			traineeDto.getReferralInfo().setComments((String) row.get(18).toString());
		}
		if (rowSize > 19 && row.get(19) != null && !row.get(19).toString().isEmpty()) {
			traineeDto.getReferralInfo().setXworkzEmail((String) row.get(19).toString());
		}
		if (rowSize > 20 && row.get(20) != null && !row.get(20).toString().isEmpty()) {
			traineeDto.getReferralInfo().setWorking((String) row.get(20).toString());
		}
		if (rowSize > 21 && row.get(21) != null && !row.get(21).toString().isEmpty()) {
			traineeDto.getReferralInfo().setPreferredLocation((String) row.get(21).toString());
		}
		if (rowSize > 22 && row.get(22) != null && !row.get(22).toString().isEmpty()) {
			traineeDto.getReferralInfo().setPreferredClassType((String) row.get(22).toString());
		}
		if (rowSize > 23 && row.get(23) != null && !row.get(23).toString().isEmpty()) {
			traineeDto.getAdminDto().setCreatedBy((String) row.get(23).toString());
		}
		if (rowSize > 24 && row.get(22) != null && !row.get(24).toString().isEmpty()) {
			traineeDto.getAdminDto().setCreatedOn((String) row.get(24).toString());
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
