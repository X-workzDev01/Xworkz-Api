package com.xworkz.dream.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.HrFollowUpDto;
@Component
public class ClientInformationImpl implements ClientInformationUtil {
	private static final Logger log = LoggerFactory.getLogger(ClientInformationImpl.class);

	@Override
	public void setValuesToClientHrDto(ClientHrDto dto) {
		if (dto.getHrScopName() == null) {
			dto.setHrScopName("NA");
		}
		if (dto.getHrEmail() == null) {
			dto.setHrEmail("NA");
		}
		if (dto.getHrContactNumber() == null) {
			dto.setHrContactNumber(0l);
		}
		if (dto.getDesignation() == null) {
			dto.setDesignation("NA");
		}
		
		if (dto.getStatus() == null) {
			dto.setStatus("NA");
		}
		if (dto.getAdminDto() == null) {
			dto.setAdminDto(new AuditDto()); // Assuming AdminDto has a no-argument constructor
		}
		if (dto.getAdminDto().getCreatedBy() == null) {
			dto.getAdminDto().setCreatedBy("NA");
		}
		dto.getAdminDto().setCreatedOn(LocalDateTime.now().toString());
	}

	@Override
	public void settingNaValues(HrFollowUpDto dto) {

		if (dto.getAttemptBy() == null) {
			dto.setAttemptBy("NA");
		}
		dto.setAttemptOn(LocalDate.now().toString());
		if (dto.getAttemptStatus() == null) {
			dto.setAttemptStatus("NA");
		}
		if (dto.getCallBackDate() == null) {
			dto.setCallBackDate("NA");
		}
		if (dto.getCallDuration() == null) {
			dto.setCallDuration(0);
		}
		if (dto.getComments() == null) {
			dto.setComments("NA");
		}
		if (dto.getCallBackTime() == null) {
			dto.setCallBackTime("NA");
		}

	}

	@Override
	public void setValuesToClientDto(ClientDto dto) {
		log.debug("client wrapper setting NA values to the fields null {}", dto);
		if (dto.getCompanyName() == null) {
			dto.setCompanyName("NA");
		}
		if (dto.getCompanyEmail() == null) {
			dto.setCompanyEmail("NA");
		}
		if (dto.getCompanyLandLineNumber() == null) {
			dto.setCompanyLandLineNumber(0l);
		}
		if (dto.getCompanyWebsite() == null) {
			dto.setCompanyWebsite("NA");
		}
		if (dto.getCompanyLocation() == null) {
			dto.setCompanyLocation("NA");
		}

		if (dto.getCompanyAddress() == null) {
			dto.setCompanyAddress("NA");
		}
		if (dto.getCompanyFounder() == null) {
			dto.setCompanyFounder("NA");
		}
		if (dto.getCompanyType() == null) {
			dto.setCompanyType("NA");
		}
		if (dto.getSourceOfConnetion() == null) {
			dto.setSourceOfConnetion("NA");
		}
		if (dto.getStatus() == null) {
			dto.setStatus("NA");
		}
		if (dto.getAdminDto() == null) {
			dto.setAdminDto(new AuditDto()); // Assuming AdminDto has a no-argument constructor
		}
		if (dto.getAdminDto().getCreatedBy() == null) {
			dto.getAdminDto().setCreatedBy("NA");
		}
		// here we need to set createdBy email
		dto.getAdminDto().setCreatedOn(LocalDateTime.now().toString());
		log.debug("client wrapper,after assigning values: {}", dto);
	}

}
