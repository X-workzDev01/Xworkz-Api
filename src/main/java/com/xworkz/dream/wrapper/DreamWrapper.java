package com.xworkz.dream.wrapper;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.xworkz.dream.constants.FollowUp;
import com.xworkz.dream.constants.RegistrationConstant;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.BasicInfoDto;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.CSR;
import com.xworkz.dream.dto.CourseDto;
import com.xworkz.dream.dto.EducationInfoDto;
import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.OthersDto;
import com.xworkz.dream.dto.StatusDto;
import com.xworkz.dream.dto.SuggestionDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.User;

@Component
public class DreamWrapper {

	public static boolean validateCell(RegistrationConstant registrationConstant) {
		return StringUtils.hasLength(String.valueOf(registrationConstant.getIndex()));
	}

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
		row.add(dto.getOthersDto().getReferalName());
		row.add(dto.getOthersDto().getReferalContactNumber());
		row.add(dto.getOthersDto().getComments());

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
		FollowUpDto followUpDto = new FollowUpDto(0, new BasicInfoDto(), null, null, null, null, null, null, null, null,
				null);
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

		if (rowSize > 9 && row.get(9) != null && !row.get(9).toString().isEmpty()) {
			followUpDto.setCallback((String) row.get(9));
		}

		if (rowSize > 10 && row.get(10) != null && !row.get(10).toString().isEmpty()) {
			if (followUpDto.getAdminDto() == null) {
				followUpDto.setAdminDto(new AuditDto());
			}
			followUpDto.getAdminDto().setCreatedBy(row.get(10).toString());
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

		// Note: The code sets the 'callback' property as a string, not a Date object.
		if (rowSize > 8 && row.get(9) != null && !row.get(9).toString().isEmpty()) {
			followUpDto.setCallback((String) row.get(9));
		}

		if (rowSize > 10 && row.get(10) != null && !row.get(10).toString().isEmpty()) {
			if (followUpDto.getAdminDto() == null) {
				followUpDto.setAdminDto(new AuditDto());
			}
			followUpDto.getAdminDto().setCreatedBy(row.get(10).toString());
		}

		if (rowSize > 11 && row.get(11) != null && !row.get(11).toString().isEmpty()) {
			if (followUpDto.getAdminDto() == null) {
				followUpDto.setAdminDto(new AuditDto());
			}
			followUpDto.getAdminDto().setCreatedOn(row.get(11).toString());
		}
		if (rowSize > 12 && row.get(12) != null && !row.get(12).toString().isEmpty()) {
			if (followUpDto.getAdminDto() == null) {
				followUpDto.setAdminDto(new AuditDto());
			}
			followUpDto.getAdminDto().setUpdatedBy(row.get(12).toString());
		}
		if (rowSize > 14 && row.get(14) != null && !row.get(14).toString().isEmpty()) {
			followUpDto.setFlag((String) row.get(14));
		}
		if (rowSize > 15 && row.get(15) != null && !row.get(15).toString().isEmpty()) {
			followUpDto.setFlagSheet((String) row.get(15));
		}
		return followUpDto;
	}

	public StatusDto listToStatusDto(List<Object> rows) {

		StatusDto statusDto = new StatusDto(0, new BasicInfoDto(), null, null, null, null, null, null, null, null,
				null);
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
			statusDto.getBasicInfo().setContactNumber(Long.parseLong(rows.get(3).toString()));
		}

		if (rowSize > 4 && rows.get(4) != null && !rows.get(4).toString().isEmpty()) {
			statusDto.setAttemptedOn((String) rows.get(4));
		}
		if (rowSize > 5 && rows.get(5) != null && !rows.get(5).toString().isEmpty()) {
			statusDto.setAttemptedBy((String) rows.get(5));
		}
		if (rowSize > 6 && rows.get(6) != null && !rows.get(6).toString().isEmpty()) {
			statusDto.setAttemptStatus((String) rows.get(6));
		}
		if (rowSize > 7 && rows.get(7) != null && !rows.get(7).toString().isEmpty()) {
			statusDto.setComments((String) rows.get(7));
		}
		if (rowSize > 8 && rows.get(8) != null && !rows.get(8).toString().isEmpty()) {
			statusDto.setCallDuration((String) rows.get(8));
		}
		if (rowSize > 9 && rows.get(9) != null && !rows.get(9).toString().isEmpty()) {
			statusDto.setCallBack((String) rows.get(9));
		}

		if (rowSize > 10 && rows.get(10) != null && !rows.get(10).toString().isEmpty()) {
			statusDto.setCallBackTime((String) rows.get(10)); // Corrected field name
		}

		return statusDto;
	}

	public TraineeDto listToDto(List<Object> row) {
		TraineeDto traineeDto = new TraineeDto(0, new BasicInfoDto(), new EducationInfoDto(), new CourseDto(),
				new OthersDto(), new AuditDto(), new CSR());
		// Assuming the list follows this order: id ,traineeName, email, contactNumber,
		// qualification, stream,
		// yearOfPassout, collegeName, batch, branch, course, referalName,
		// referalContactNumber, comments
		// if there any changes in the table, please make sure the right changes are
		// done here also
		
		if (validateCell(RegistrationConstant.COLUMN_ID)) {
			traineeDto.setId(Integer.valueOf(row.get(RegistrationConstant.COLUMN_ID.getIndex()).toString()));
		}

		if (validateCell(RegistrationConstant.COLUMN_TRAINEE_NAME)) {
			traineeDto.getBasicInfo()
					.setTraineeName(row.get(RegistrationConstant.COLUMN_TRAINEE_NAME.getIndex()).toString());
		}

		if (validateCell(RegistrationConstant.COLUMN_EMAIL)) {
			traineeDto.getBasicInfo()
					.setEmail((String) row.get(RegistrationConstant.COLUMN_EMAIL.getIndex()).toString());
		}

		
		if (validateCell(RegistrationConstant.COLUMN_CONTACT_NUMBER)) {
			traineeDto.getBasicInfo().setContactNumber(
					Long.parseLong(row.get(RegistrationConstant.COLUMN_CONTACT_NUMBER.getIndex()).toString()));
		}

		if (validateCell(RegistrationConstant.COLUMN_DATE_OF_BIRTH)) {
			traineeDto.getBasicInfo()
					.setDateOfBirth(row.get(RegistrationConstant.COLUMN_DATE_OF_BIRTH.getIndex()).toString());
		}

		if (validateCell(RegistrationConstant.COLUMN_QUALIFICATION)) {
			traineeDto.getEducationInfo()
					.setQualification(row.get(RegistrationConstant.COLUMN_QUALIFICATION.getIndex()).toString());
		}

		if (validateCell(RegistrationConstant.COLUMN_STREAM)) {
			traineeDto.getEducationInfo().setStream((String) row.get(RegistrationConstant.COLUMN_STREAM.getIndex()));
		}

		if (validateCell(RegistrationConstant.COLUMN_YEAR_OF_PASSOUT)) {
			traineeDto.getEducationInfo()
					.setYearOfPassout((String) row.get(RegistrationConstant.COLUMN_YEAR_OF_PASSOUT.getIndex()));
		}

		if (validateCell(RegistrationConstant.COLUMN_COLLEGE_NAME)) {
			traineeDto.getEducationInfo()
					.setCollegeName((String) row.get(RegistrationConstant.COLUMN_COLLEGE_NAME.getIndex()));
		}

		if (validateCell(RegistrationConstant.COLUMN_COURSE)) {
			traineeDto.getCourseInfo().setCourse((String) row.get(RegistrationConstant.COLUMN_COURSE.getIndex()));
		}

		if (validateCell(RegistrationConstant.COLUMN_BRANCH)) {
			traineeDto.getCourseInfo().setBranch((String) row.get(RegistrationConstant.COLUMN_BRANCH.getIndex()));
		}

		if (validateCell(RegistrationConstant.COLUMN_TRAINER_NAME)) {
			traineeDto.getCourseInfo()
					.setTrainerName((String) row.get(RegistrationConstant.COLUMN_TRAINER_NAME.getIndex()));
		}
		if (validateCell(RegistrationConstant.COLUMN_BATCH_TYPE)) {
			traineeDto.getCourseInfo()
					.setBatchType((String) row.get(RegistrationConstant.COLUMN_BATCH_TYPE.getIndex()));
		}
		if (validateCell(RegistrationConstant.COLUMN_BATCH_TIMING)) {
			traineeDto.getCourseInfo()
					.setBatchTiming((String) row.get(RegistrationConstant.COLUMN_BATCH_TIMING.getIndex()));
		}
		if (validateCell(RegistrationConstant.COLUMN_START_DATE)) {
			traineeDto.getCourseInfo()
					.setStartDate((String) row.get(RegistrationConstant.COLUMN_START_DATE.getIndex()));
		}

		if (validateCell(RegistrationConstant.COLUMN_OFFERED_AS)) {
			traineeDto.getCourseInfo()
					.setOfferedAs((String) (row.get(RegistrationConstant.COLUMN_OFFERED_AS.getIndex())));
		}

		if (validateCell(RegistrationConstant.COLUMN_REFERRAL_NAME)) {
			traineeDto.getOthersDto()
					.setReferalName((String) row.get(RegistrationConstant.COLUMN_REFERRAL_NAME.getIndex()));
		}
		if (validateCell(RegistrationConstant.COLUMN_REFERRAL_CONTACT_NUMBER)) {
			Long referalContactNumber = Long
					.parseLong(row.get(RegistrationConstant.COLUMN_REFERRAL_CONTACT_NUMBER.getIndex()).toString());
			traineeDto.getOthersDto().setReferalContactNumber(referalContactNumber);
		}
		if (validateCell(RegistrationConstant.COLUMN_COMMENTS)) {
			traineeDto.getOthersDto().setComments((String) row.get(RegistrationConstant.COLUMN_COMMENTS.getIndex()));
		}
		if (validateCell(RegistrationConstant.COLUMN_XWORKZ_EMAIL)) {
			traineeDto.getOthersDto()
					.setXworkzEmail((String) row.get(RegistrationConstant.COLUMN_XWORKZ_EMAIL.getIndex()));
		}
		if (validateCell(RegistrationConstant.COLUMN_WORKING)) {
			traineeDto.getOthersDto().setWorking((String) row.get(RegistrationConstant.COLUMN_WORKING.getIndex()));
		}
		if (validateCell(RegistrationConstant.COLUMN_PREFERRED_LOCATION)) {
			traineeDto.getOthersDto()
					.setPreferredLocation((String) row.get(RegistrationConstant.COLUMN_PREFERRED_LOCATION.getIndex()));
		}
		if (validateCell(RegistrationConstant.COLUMN_PREFERRED_CLASS_TYPE)) {
			traineeDto.getOthersDto().setPreferredClassType(
					(String) row.get(RegistrationConstant.COLUMN_PREFERRED_CLASS_TYPE.getIndex()));
		}
		if (validateCell(RegistrationConstant.COLUMN_SEND_WHATSAPP_LINK)) {
			traineeDto.getOthersDto()
					.setSendWhatsAppLink((String) row.get(RegistrationConstant.COLUMN_SEND_WHATSAPP_LINK.getIndex()));

		}
		if (validateCell(RegistrationConstant.COLUMN_REGISTRATION_DATE)) {
			traineeDto.getOthersDto()
					.setRegistrationDate((String) row.get(RegistrationConstant.COLUMN_REGISTRATION_DATE.getIndex()));
		}
		if (validateCell(RegistrationConstant.COLUMN_CREATED_BY)) {
			traineeDto.getAdminDto()
					.setCreatedBy(row.get(RegistrationConstant.COLUMN_CREATED_BY.getIndex()).toString());
		}

		if (validateCell(RegistrationConstant.COLUMN_CREATED_ON)) {
			String createdOnValue = row.get(RegistrationConstant.COLUMN_CREATED_ON.getIndex()).toString();
			traineeDto.getAdminDto().setCreatedOn(createdOnValue);
		}

		if (validateCell(RegistrationConstant.COLUMN_UPDATED_BY)) {
			traineeDto.getAdminDto()
					.setUpdatedBy(row.get(RegistrationConstant.COLUMN_UPDATED_BY.getIndex()).toString());
		}

		if (validateCell(RegistrationConstant.COLUMN_UPDATED_ON)) {
			traineeDto.getAdminDto()
					.setUpdatedOn(row.get(RegistrationConstant.COLUMN_UPDATED_ON.getIndex()).toString());
		}
		if (validateCell(RegistrationConstant.COLUMN_USN_NUMBER)) {
			traineeDto.getCsrDto().setUsnNumber(row.get(RegistrationConstant.COLUMN_USN_NUMBER.getIndex()).toString());
		}
		if (validateCell(RegistrationConstant.COLUMN_ALTERNATIVE_CONTACT_NUMBER)) {
			Long alternativeContactNumber = Long
					.parseLong(row.get(RegistrationConstant.COLUMN_ALTERNATIVE_CONTACT_NUMBER.getIndex()).toString());
			traineeDto.getCsrDto().setAlternateContactNumber(alternativeContactNumber);
		}
		if (validateCell(RegistrationConstant.COLUMN_UNIQUE_ID)) {
			traineeDto.getCsrDto().setUniqueId(row.get(RegistrationConstant.COLUMN_UNIQUE_ID.getIndex()).toString());
		}
		if (validateCell(RegistrationConstant.COLUMN_CSR_FLAG)) {
			traineeDto.getCsrDto().setCsrFlag(row.get(RegistrationConstant.COLUMN_CSR_FLAG.getIndex()).toString());
		}
		if (validateCell(RegistrationConstant.COLUMN_ACTIVE_FLAG)) {
			traineeDto.getCsrDto()
					.setActiveFlag(row.get(RegistrationConstant.COLUMN_ACTIVE_FLAG.getIndex()).toString());
		}
		return traineeDto;
	}

	public static List<Object> userToList(User user) {
		List<Object> row = new ArrayList<>();
		row.add(user.getEmail());
		row.add(user.getLoginTime());
		return row;

	}

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

	public List<Object> listOfBatchDetails(BatchDetailsDto dto) {
		List<Object> row = new ArrayList<Object>();
		row.add(dto.getId());
		row.add(dto.getCourseName());
		row.add(dto.getTrainerName());
		row.add(dto.getStartDate());
		row.add(dto.getBatchType());
		row.add(dto.getStartTime());
		row.add(dto.getBranchName());
		row.add(dto.getBatchStatus());
		row.add(dto.getWhatsAppLink());

		return row;
	}

	public BatchDetailsDto batchDetailsToDto(List<Object> row) {

		BatchDetailsDto details = new BatchDetailsDto(0, null, null, null, null, null, null, null, null, null, null);
		int rowSize = row.size();
		if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
			details.setId(Integer.valueOf(row.get(0).toString()));
		}
		if (rowSize > 1 && row.get(1) != null && !row.get(1).toString().isEmpty()) {
			details.setCourseName(String.valueOf(row.get(1).toString()));
		}
		if (rowSize > 2 && row.get(2) != null && !row.get(2).toString().isEmpty()) {
			details.setTrainerName(String.valueOf(row.get(2).toString()));
		}
		if (rowSize > 3 && row.get(3) != null && !row.get(3).toString().isEmpty()) {
			details.setStartDate(String.valueOf(row.get(3).toString()));
		}
		if (rowSize > 4 && row.get(4) != null && !row.get(4).toString().isEmpty()) {
			details.setBatchType(String.valueOf(row.get(4).toString()));
		}
		if (rowSize > 5 && row.get(5) != null && !row.get(5).toString().isEmpty()) {
			details.setStartTime(String.valueOf(row.get(5).toString()));
		}
		if (rowSize > 6 && row.get(6) != null && !row.get(6).toString().isEmpty()) {
			details.setBranchName(String.valueOf(row.get(6).toString()));
		}
		if (rowSize > 7 && row.get(7) != null && !row.get(7).toString().isEmpty()) {
			details.setBatchStatus(String.valueOf(row.get(7).toString()));
		}
		if (rowSize > 8 && row.get(8) != null && !row.get(8).toString().isEmpty()) {
			details.setWhatsAppLink(String.valueOf(row.get(8).toString()));
		}
		if (rowSize > 9 && row.get(9) != null && !row.get(9).toString().isEmpty()) {
			Long totalAmount = Long.parseLong(row.get(9).toString());
			details.setTotalAmount(totalAmount);
		}
		if (rowSize > 10 && row.get(10) != null && !row.get(10).toString().isEmpty()) {
			details.setTotalClass(Integer.valueOf(row.get(10).toString()));
		}

		return details;
	}

	public List<Object> listOfAttendance(AttendanceDto dto) {
		List<Object> row = new ArrayList<Object>();
		row.add(dto.getAttendanceId());
		row.add(dto.getId());
		row.add(dto.getTraineeName());
		row.add(dto.getCourse());
		row.add(dto.getTotalAbsent());
		row.add(dto.getAbsentDate());
		row.add(dto.getReason());
		row.add(dto.getAdminDto().getCreatedBy());
		row.add(dto.getAdminDto().getCreatedOn());
		return row;

	}

	public AttendanceDto attendanceListToDto(List<Object> row) {
		AttendanceDto attendanceDto = new AttendanceDto(null, null, null, null, null, null, null, null, new AuditDto());
		int rowSize = row.size();
		if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
			attendanceDto.setAttendanceId(Integer.valueOf(row.get(0).toString()));
		}
		if (rowSize > 1 && row.get(1) != null && !row.get(1).toString().isEmpty()) {
			attendanceDto.setId(Integer.valueOf(row.get(1).toString()));
		}
		if (rowSize > 2 && row.get(2) != null && !row.get(2).toString().isEmpty()) {
			attendanceDto.setTraineeName(String.valueOf(row.get(2).toString()));
		}
		if (rowSize > 3 && row.get(3) != null && !row.get(3).toString().isEmpty()) {
			attendanceDto.setCourse(String.valueOf(row.get(3).toString()));
		}
		if (rowSize > 4 && row.get(4) != null && !row.get(4).toString().isEmpty()) {
			attendanceDto.setTotalAbsent(Integer.valueOf(row.get(4).toString()));
		}
		if (rowSize > 5 && row.get(5) != null && !row.get(5).toString().isEmpty()) {
			attendanceDto.setAbsentDate(String.valueOf(row.get(5).toString()));
		}
		if (rowSize > 6 && row.get(6) != null && !row.get(6).toString().isEmpty()) {
			attendanceDto.setReason(String.valueOf(row.get(6).toString()));
		}
		if (rowSize > 7 && row.get(7) != null && !row.get(7).toString().isEmpty()) {
			attendanceDto.getAdminDto().setCreatedBy(row.get(7).toString());
		}

		if (rowSize > 8 && row.get(8) != null && !row.get(8).toString().isEmpty()) {
			String createdOnValue = row.get(8).toString();
			attendanceDto.getAdminDto().setCreatedOn(createdOnValue);
		}

		if (rowSize > 9 && row.get(9) != null && !row.get(9).toString().isEmpty()) {
			attendanceDto.getAdminDto().setUpdatedBy(row.get(9).toString());
		}

		if (rowSize > 10 && row.get(10) != null && !row.get(10).toString().isEmpty()) {
			attendanceDto.getAdminDto().setUpdatedOn(row.get(10).toString());
		}
		return attendanceDto;
	}

	public void setValueAttendaceDto(AttendanceDto dto) {
		if (dto.getTotalAbsent() == null) {
			dto.setTotalAbsent(0);
		}
		if (dto.getAbsentDate() == null) {
			dto.setAbsentDate("NA");
		}
		if (dto.getReason() == null) {
			dto.setReason("NA");
		}
		if (dto.getAdminDto().getCreatedOn() == null) {
			dto.getAdminDto().setCreatedOn(LocalDate.now().toString());
		}

	}

	public EnquiryDto validateEnquiry(EnquiryDto dto) {
		BasicInfoDto basicDto = dto.getBasicInfo();
		if (basicDto != null) {
			basicDto.setDateOfBirth("NA");

			if (basicDto.getEmail() == null || basicDto.getEmail().isEmpty()) {
				String contactNumber = String.valueOf(basicDto.getContactNumber());
				String generatedEmail = contactNumber + "@dummy.com";
				basicDto.setEmail(generatedEmail);
			}
		}

		EducationInfoDto educationDto = dto.getEducationInfo();
		if (educationDto != null) {
			if (educationDto.getCollegeName() == null || educationDto.getCollegeName().isEmpty()) {
				educationDto.setCollegeName("NA");
			}

			if (educationDto.getStream() == null || educationDto.getStream().isEmpty()) {
				educationDto.setStream("NA");
			}

			if (educationDto.getQualification() == null || educationDto.getQualification().isEmpty()) {
				educationDto.setQualification("NA");
			}

			if (educationDto.getYearOfPassout() == null || educationDto.getYearOfPassout().isEmpty()) {
				educationDto.setYearOfPassout("NA");
			}
		}

		return dto;
	}

	public FollowUpDto setFollowUp(TraineeDto traineeDto) {
		FollowUpDto followUpDto = new FollowUpDto();
		BasicInfoDto basicInfo = new BasicInfoDto();
		basicInfo.setTraineeName(traineeDto.getBasicInfo().getTraineeName());
		basicInfo.setEmail(traineeDto.getBasicInfo().getEmail());
		basicInfo.setContactNumber(traineeDto.getBasicInfo().getContactNumber());
		followUpDto.setBasicInfo(basicInfo);
		followUpDto.setCourseName(traineeDto.getCourseInfo().getCourse());
		followUpDto.setRegistrationDate(LocalDateTime.now().toString());
		followUpDto.setJoiningDate(FollowUp.NOT_CONFIRMED.toString());
		followUpDto.setId(traineeDto.getId());
		followUpDto.setCurrentlyFollowedBy(FollowUp.NONE.toString());
		followUpDto.setCurrentStatus(FollowUp.NEW.toString());
		followUpDto.setAdminDto(traineeDto.getAdminDto());
		followUpDto.getAdminDto().setUpdatedBy("NA");
		followUpDto.getAdminDto().setUpdatedOn("NA");
		followUpDto.setFlagSheet("Active");
		followUpDto.setFlag("Active");

		return followUpDto;
	}

	public FollowUpDto setFollowUpEnquiry(TraineeDto traineeDto) {
		FollowUpDto followUpDto = new FollowUpDto();
		BasicInfoDto basicInfo = new BasicInfoDto();
		basicInfo.setTraineeName(traineeDto.getBasicInfo().getTraineeName());
		basicInfo.setEmail(traineeDto.getBasicInfo().getEmail());
		basicInfo.setContactNumber(traineeDto.getBasicInfo().getContactNumber());
		followUpDto.setBasicInfo(basicInfo);
		followUpDto.setCourseName(traineeDto.getCourseInfo().getCourse());
		followUpDto.setRegistrationDate(LocalDateTime.now().toString());
		followUpDto.setJoiningDate(FollowUp.NOT_CONFIRMED.toString());
		followUpDto.setId(traineeDto.getId());
		followUpDto.setCurrentlyFollowedBy(FollowUp.NONE.toString());
		followUpDto.setCurrentStatus(FollowUp.ENQUIRY.toString());
		followUpDto.setAdminDto(traineeDto.getAdminDto());
		followUpDto.getAdminDto().setUpdatedBy("NA");
		followUpDto.getAdminDto().setUpdatedOn("NA");
		followUpDto.setFlag("Active");
		followUpDto.setFlagSheet("Active");


		return followUpDto;
	}

	public FollowUpDto setFollowUpCSR(TraineeDto traineeDto) {
		FollowUpDto followUpDto = new FollowUpDto();
		BasicInfoDto basicInfo = new BasicInfoDto();
		basicInfo.setTraineeName(traineeDto.getBasicInfo().getTraineeName());
		basicInfo.setEmail(traineeDto.getBasicInfo().getEmail());
		basicInfo.setContactNumber(traineeDto.getBasicInfo().getContactNumber());
		followUpDto.setBasicInfo(basicInfo);
		followUpDto.setCourseName(traineeDto.getCourseInfo().getCourse());
		followUpDto.setRegistrationDate(LocalDateTime.now().toString());
		followUpDto.setJoiningDate(FollowUp.NOT_CONFIRMED.toString());
		followUpDto.setId(traineeDto.getId());
		followUpDto.setCurrentlyFollowedBy(FollowUp.NONE.toString());
		followUpDto.setCurrentStatus(traineeDto.getCourseInfo().getOfferedAs());
		followUpDto.setAdminDto(traineeDto.getAdminDto());
		followUpDto.getAdminDto().setUpdatedBy("NA");
		followUpDto.getAdminDto().setUpdatedOn("NA");
		followUpDto.setFlag("Active");
		followUpDto.setFlagSheet("Active");


		return followUpDto;
	}

	public void setAdminDto(TraineeDto dto) {
		AuditDto admin = new AuditDto();
		admin.setCreatedBy(dto.getAdminDto().getCreatedBy());
		admin.setCreatedOn(dto.getAdminDto().getCreatedOn());
		admin.setUpdatedBy(dto.getAdminDto().getUpdatedBy());
		admin.setUpdatedOn(LocalDateTime.now().toString());
		dto.setAdminDto(admin);
	}

	public StatusDto setFollowUpStatus(StatusDto statusDto, List<List<Object>> data) {
		int size = data != null ? data.size() : 0;
		BasicInfoDto basicInfo = new BasicInfoDto();
		basicInfo.setTraineeName(statusDto.getBasicInfo().getTraineeName());
		basicInfo.setEmail(statusDto.getBasicInfo().getEmail());
		basicInfo.setContactNumber(statusDto.getBasicInfo().getContactNumber());
		StatusDto sdto = new StatusDto();
		sdto.setId(size += 1);
		sdto.setBasicInfo(basicInfo);
		sdto.setAttemptedOn(LocalDateTime.now().toString());
		sdto.setAttemptedBy(statusDto.getAttemptedBy());
		sdto.setAttemptStatus(statusDto.getAttemptStatus());
		sdto.setComments(statusDto.getComments());
		sdto.setCallDuration(statusDto.getCallDuration());
		if (statusDto.getCallBack().equals("NA")) {
			sdto.setCallBack(LocalDate.now().plusDays(1).toString());
		} else {
			sdto.setCallBack(statusDto.getCallBack());
		}
		sdto.setCallBackTime(statusDto.getCallBackTime());
		sdto.setJoiningDate(statusDto.getJoiningDate());
		return sdto;
	}

	public void setFieldValueAsNa(TraineeDto dto) {
		if (dto.getCourseInfo().getCourse() == null) {
			dto.getCourseInfo().setCourse("NA");

		}
		if (dto.getCourseInfo().getBranch() == null) {
			dto.getCourseInfo().setBranch("NA");
		}
		if (dto.getCourseInfo().getTrainerName() == null) {
			dto.getCourseInfo().setTrainerName("NA");
		}
		if (dto.getCourseInfo().getBatchType() == null) {
			dto.getCourseInfo().setBatchType("NA");

		}
		if (dto.getCourseInfo().getBatchTiming() == null) {
			dto.getCourseInfo().setBatchTiming("NA");
		}
		if (dto.getCourseInfo().getStartDate() == null) {
			dto.getCourseInfo().setStartDate("NA");
		}
	}

	public void setValuesForTraineeDto(TraineeDto dto) {
		dto.getOthersDto().setXworkzEmail(Status.NA.toString());
		dto.getOthersDto().setPreferredLocation(Status.NA.toString());
		dto.getOthersDto().setPreferredClassType(Status.NA.toString());
		dto.getOthersDto().setSendWhatsAppLink(Status.NO.toString());
		dto.getOthersDto().setRegistrationDate(LocalDateTime.now().toString());
		dto.getAdminDto().setCreatedOn(LocalDateTime.now().toString());
		dto.getAdminDto().setUpdatedBy("NA");
		dto.getAdminDto().setUpdatedOn("NA");

		if (dto.getOthersDto().getReferalName() == null) {
			dto.getOthersDto().setReferalName(Status.NA.toString());

		}
		if (dto.getOthersDto().getComments() == null) {
			dto.getOthersDto().setComments(Status.NA.toString());
		}
		if (dto.getOthersDto().getWorking() == null) {

			dto.getOthersDto().setWorking(Status.NA.toString());
		}
		if (dto.getOthersDto().getReferalContactNumber() == null) {

			dto.getOthersDto().setReferalContactNumber(0L);
		}

	}

	public void setValuesForCSRDto(TraineeDto dto) {
		dto.getBasicInfo().setDateOfBirth(Status.NA.toString());
		dto.getOthersDto().setXworkzEmail(Status.NA.toString());
		dto.getOthersDto().setPreferredLocation(Status.NA.toString());
		dto.getOthersDto().setPreferredClassType(Status.NA.toString());
		dto.getOthersDto().setSendWhatsAppLink(Status.NO.toString());
		dto.getOthersDto().setRegistrationDate(LocalDateTime.now().toString());
		if (dto.getOthersDto().getReferalName() == null) {
			dto.getOthersDto().setReferalName(Status.NA.toString());
		}
		if (dto.getOthersDto().getComments() == null) {
			dto.getOthersDto().setComments(Status.NA.toString());
		}
		if (dto.getOthersDto().getWorking() == null) {

			dto.getOthersDto().setWorking(Status.NA.toString());
		}
		if (dto.getOthersDto().getReferalContactNumber() == null) {

			dto.getOthersDto().setReferalContactNumber(0L);
		}
		AuditDto admin = new AuditDto();
		if (dto.getAdminDto() == null) {
			admin.setCreatedBy(dto.getBasicInfo().getTraineeName());
			admin.setCreatedOn(LocalDateTime.now().toString());
			admin.setUpdatedBy(Status.NA.toString());
			admin.setUpdatedOn(Status.NA.toString());
			dto.setAdminDto(admin);
		} else {
			admin.setCreatedBy(dto.getAdminDto().getCreatedBy());
			admin.setCreatedOn(dto.getAdminDto().getCreatedOn());
			admin.setUpdatedBy("NA");
			admin.setUpdatedOn("NA");
			dto.setAdminDto(admin);
		}

	}

	public AttendanceDto saveAttendance(TraineeDto dto) {
		System.err.println("Dto in wrapper attendance wrapper : " + dto);
		AttendanceDto attendanceDto = new AttendanceDto();
		attendanceDto.setId(dto.getId());
		attendanceDto.setCourse(dto.getCourseInfo().getCourse());
		attendanceDto.setTraineeName(dto.getBasicInfo().getTraineeName());
		if (attendanceDto.getTotalAbsent() == null) {
			attendanceDto.setTotalAbsent(0);
		}
		if (attendanceDto.getAbsentDate() == null) {
			attendanceDto.setAbsentDate("NA");
		}
		if (attendanceDto.getReason() == null) {
			attendanceDto.setReason("NA");
		}
		AuditDto admin = new AuditDto();
		admin.setCreatedBy("Swagger");
		admin.setCreatedOn(LocalDate.now().toString());
		admin.setUpdatedBy("NA");
		admin.setUpdatedOn("NA");
		attendanceDto.setAdminDto(admin);

		return attendanceDto;

	}

}
