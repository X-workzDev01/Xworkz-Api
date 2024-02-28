package com.xworkz.dream.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.HrFollowUpDto;

@Component
public class ClientInformationImpl implements ClientInformationUtil {
	private static final Logger log = LoggerFactory.getLogger(ClientInformationImpl.class);

	@Override
	public void setValuesToClientHrDto(ClientHrDto dto) {
		if (dto.getHrSpocName() == null || dto.getHrSpocName().isEmpty()) {
			dto.setHrSpocName(ServiceConstant.NA.toString());
		}
		if (dto.getHrEmail() == null || dto.getHrEmail().isEmpty()) {
			dto.setHrEmail(ServiceConstant.NA.toString());
		}
		if (dto.getHrContactNumber() == null) {
			dto.setHrContactNumber(0l);
		}
		if (dto.getDesignation() == null || dto.getDesignation().isEmpty()) {
			dto.setDesignation(ServiceConstant.NA.toString());
		}

		if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
			dto.setStatus(ServiceConstant.NA.toString());
		}
		if (dto.getAdminDto() == null) {
			dto.setAdminDto(new AuditDto()); // Assuming AdminDto has a no-argument constructor
		}
		if (dto.getAdminDto().getCreatedBy() == null) {
			dto.getAdminDto().setCreatedBy(ServiceConstant.NA.toString());
		}
		dto.getAdminDto().setCreatedOn(LocalDateTime.now().toString());
	}

	@Override
	public void settingNaValues(HrFollowUpDto dto) {
		if (dto.getAttemptBy() == null || dto.getAttemptBy().isEmpty()) {
			dto.setAttemptBy(ServiceConstant.NA.toString());
		}
		dto.setAttemptOn(LocalDateTime.now().toString());
		if (dto.getAttemptStatus() == null) {
			dto.setAttemptStatus(ServiceConstant.NA.toString());
		}
		if (dto.getCallBackDate() == null || dto.getCallBackDate().isEmpty()) {
			dto.setCallBackDate(LocalDate.now().plusDays(1).toString());
		}
		if (dto.getCallBackDate().equals(ServiceConstant.NA.toString())) {
			dto.setCallBackDate(LocalDate.now().plusDays(1).toString());
		} else {
			dto.setCallBackDate(dto.getCallBackDate());
		}
		if (dto.getCallBackTime() != null) {
			dto.setCallBackTime(ServiceConstant.NA.toString());
		}
		if (dto.getCallDuration() == null) {
			dto.setCallDuration(0);
		}
		if (dto.getComments() == null || dto.getComments().isEmpty()) {
			dto.setComments(ServiceConstant.NA.toString());
		}
		if (dto.getCallBackTime() == null || dto.getComments().isEmpty()) {
			dto.setCallBackTime(ServiceConstant.NA.toString());
		}
	}

	@Override
	public void setValuesToClientDto(ClientDto dto) {
		log.debug("client wrapper setting NA values to the fields null {}", dto);
		if (dto.getCompanyName() == null || dto.getCompanyName().isEmpty()) {
			dto.setCompanyName(ServiceConstant.NA.toString());
		}
		if (dto.getCompanyEmail() == null || dto.getCompanyEmail().isEmpty()) {
			dto.setCompanyEmail(ServiceConstant.NA.toString());
		}
		if (dto.getCompanyLandLineNumber() == null) {
			dto.setCompanyLandLineNumber(0l);
		}
		if (dto.getCompanyWebsite() == null || dto.getCompanyWebsite().isEmpty()) {
			dto.setCompanyWebsite(ServiceConstant.NA.toString());
		}
		if (dto.getCompanyLocation() == null || dto.getCompanyLocation().isEmpty()) {
			dto.setCompanyLocation(ServiceConstant.NA.toString());
		}

		if (dto.getCompanyAddress() == null || dto.getCompanyAddress().isEmpty()) {
			dto.setCompanyAddress(ServiceConstant.NA.toString());
		}
		if (dto.getCompanyFounder() == null || dto.getCompanyFounder().isEmpty()) {
			dto.setCompanyFounder(ServiceConstant.NA.toString());
		}
		if (dto.getCompanyType() == null || dto.getCompanyType().isEmpty()) {
			dto.setCompanyType(ServiceConstant.NA.toString());
		}
		if (dto.getSourceOfConnection() == null || dto.getSourceOfConnection().isEmpty()) {
			dto.setSourceOfConnection(ServiceConstant.NA.toString());
		}
		if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
			dto.setStatus(ServiceConstant.NA.toString());
		}
		if (dto.getAdminDto() == null) {
			dto.setAdminDto(new AuditDto()); // Assuming AdminDto has a no-argument constructor
		}
		if (dto.getAdminDto().getCreatedBy() == null) {
			dto.getAdminDto().setCreatedBy(ServiceConstant.NA.toString());
		}
		// here we need to set createdBy email
		dto.getAdminDto().setCreatedOn(LocalDateTime.now().toString());
		log.debug("client wrapper,after assigning values: {}", dto);
	}

}
