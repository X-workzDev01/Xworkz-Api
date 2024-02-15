package com.xworkz.dream.wrapper;

import org.springframework.stereotype.Component;

import com.xworkz.dream.dto.ExportDto;
import com.xworkz.dream.dto.TraineeDto;

@Component
public class ExportWrapperImpl implements ExportWrapper {

	@Override
	public ExportDto assignToExportDto(TraineeDto dto) {
		ExportDto exportDto = new ExportDto();
		exportDto.setTraineeName(dto.getBasicInfo().getTraineeName());
		exportDto.setTraineeEmail(dto.getBasicInfo().getEmail());
		exportDto.setContactNumber(dto.getBasicInfo().getContactNumber());
		exportDto.setDateOfBirth(dto.getBasicInfo().getDateOfBirth());
		exportDto.setQualification(dto.getEducationInfo().getQualification());
		exportDto.setCollegeName(dto.getEducationInfo().getCollegeName());
		exportDto.setStream(dto.getEducationInfo().getStream());
		exportDto.setOfferedAs(dto.getCourseInfo().getOfferedAs());
		exportDto.setCourseName(dto.getCourseInfo().getCourse());
		exportDto.setYearOfPassout(dto.getEducationInfo().getYearOfPassout());
		exportDto.setXworkzEmailId(dto.getOthersDto().getXworkzEmail());
		exportDto.setRegistrationDate(dto.getOthersDto().getRegistrationDate());
		exportDto.setWorking(dto.getOthersDto().getWorking());
		exportDto.setUniqueNumber(dto.getCsrDto().getUniqueId());
		exportDto.setUsnNumber(dto.getCsrDto().getUsnNumber());
		exportDto.setWhatsAppNumber(dto.getCsrDto().getAlternateContactNumber());
		exportDto.setBatchType(dto.getCourseInfo().getBatchType());
		exportDto.setBatchStartDate(dto.getCourseInfo().getStartDate());
		exportDto.setBatchTiming(dto.getCourseInfo().getBatchTiming());
		exportDto.setBranch(dto.getCourseInfo().getBranch());
		exportDto.setTrainerName(dto.getCourseInfo().getTrainerName());
		exportDto.setPreferedClassType(dto.getOthersDto().getPreferredClassType());
		exportDto.setPreferedLocation(dto.getOthersDto().getPreferredLocation());
		exportDto.setReferalContactNumber(dto.getOthersDto().getReferalContactNumber());
		exportDto.setReferalName(dto.getOthersDto().getReferalName());
		exportDto.setWorking(dto.getOthersDto().getWorking());
		exportDto.setCreatedBy(dto.getAdminDto().getCreatedBy());
		exportDto.setCreatedOn(dto.getAdminDto().getCreatedOn());
		exportDto.setUpdatedBy(dto.getAdminDto().getUpdatedBy());
		exportDto.setUpdatedOn(dto.getAdminDto().getUpdatedOn());
		return exportDto;
	}

}
