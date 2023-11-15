package com.xworkz.dream.wrapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.AdminDto;
import com.xworkz.dream.dto.ClientDto;

@Service
public class ClientWrapperImpl implements ClientWrapper {
	private static final Logger log = LoggerFactory.getLogger(ClientWrapper.class);

	@Override
	public void setValuesToClientDto(ClientDto dto) {
		log.info("client wrapper setting NA values to the fields null {}", dto);
		if (dto.getCompanyName() == null) {
			dto.setCompanyName("NA");
		}
		if (dto.getHrScop() == null) {
			dto.setHrScop("NA");
		}
		if (dto.getHrContactNumber() == null) {
			dto.setHrContactNumber(0L);
		}
		if (dto.getHrMailId() == null) {
			dto.setHrMailId("NA");
		}
		if (dto.getCompanyLandLine() == null) {
			dto.setCompanyLandLine(0L);
		}
		if (dto.getLocation() == null) {
			dto.setLocation("NA");
		}
		if (dto.getStatus() == null) {
			dto.setStatus("NA");
		}
		if(dto.getComments()==null) {
			dto.setComments("NA");
		}
		dto.setRegistrationDate(LocalDate.now().toString());
		if (dto.getAdminDto() == null) {
			dto.setAdminDto(new AdminDto()); // Assuming AdminDto has a no-argument constructor
		}
		if (dto.getAdminDto().getCreatedBy() == null) {
			dto.getAdminDto().setCreatedBy("NA");
		}
		// here we need to set createdBy email
		dto.getAdminDto().setCreatedOn(LocalDateTime.now().toString());
		log.debug("client wrapper,after assigning values: {}", dto);
	}

	@Override
	public ClientDto listToClientDto(List<Object> row) {

		ClientDto clientDto = new ClientDto();
		int rowSize = row.size();
		// Set clientDto properties based on the elements in the input list
		if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
			clientDto.setId(Integer.valueOf(row.get(0).toString()));
		}
		if (rowSize > 1 && row.get(1) != null && !row.get(1).toString().isEmpty()) {
			clientDto.setCompanyName((String) row.get(1));
		}
		if (rowSize > 2 && row.get(2) != null && !row.get(2).toString().isEmpty()) {
			clientDto.setHrScop((String) row.get(2));
		}
		if (rowSize > 3 && row.get(3) != null && !row.get(3).toString().isEmpty()) {
			clientDto.setHrContactNumber(Long.parseLong(row.get(3).toString()));
		}

		if (rowSize > 4 && row.get(4) != null && !row.get(4).toString().isEmpty()) {
			clientDto.setHrMailId((String) row.get(4));
		}
		if (rowSize > 5 && row.get(5) != null && !row.get(5).toString().isEmpty()) {
			clientDto.setHrContactNumber(Long.parseLong(row.get(5).toString()));
		}

		if (rowSize > 6 && row.get(6) != null && !row.get(6).toString().isEmpty()) {
			clientDto.setLocation((String) row.get(6));
		}
		if (rowSize > 7 && row.get(7) != null && !row.get(7).toString().isEmpty()) {
			clientDto.setStatus((String) row.get(7));
		}
		if (rowSize > 8 && row.get(8) != null && !row.get(8).toString().isEmpty()) {
			clientDto.setRegistrationDate((String) row.get(8));
		}
		if (rowSize > 9 && row.get(9) != null && !row.get(9).toString().isEmpty()) {
			clientDto.setComments((String) row.get(9));
		}
		if (rowSize > 10 && row.get(10) != null && !row.get(10).toString().isEmpty()) {
			if (clientDto.getAdminDto() == null) {
				clientDto.setAdminDto(new AdminDto());
			}
			clientDto.getAdminDto().setCreatedBy((String) row.get(10));
		}
		if (rowSize > 11 && row.get(11) != null && !row.get(11).toString().isEmpty()) {
			if (clientDto.getAdminDto() == null) {
				clientDto.setAdminDto(new AdminDto());
			}
			clientDto.getAdminDto().setCreatedOn((String) row.get(11));
		}
		if (rowSize > 12 && row.get(12) != null && !row.get(12).toString().isEmpty()) {
			if (clientDto.getAdminDto() == null) {
				clientDto.setAdminDto(new AdminDto());
			}
			clientDto.getAdminDto().setUpdatedBy((String) row.get(12));
		}
		if (rowSize > 12 && row.get(12) != null && !row.get(12).toString().isEmpty()) {
			if (clientDto.getAdminDto() == null) {
				clientDto.setAdminDto(new AdminDto());
			}
			clientDto.getAdminDto().setUpdatedOn((String) row.get(12));
		}
		log.info("clinet wrapper assigning list value to the dto {}", clientDto);
		return clientDto;
	}

}
