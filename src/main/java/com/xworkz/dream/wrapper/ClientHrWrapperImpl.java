package com.xworkz.dream.wrapper;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.ClientHrDto;

@Service
public class ClientHrWrapperImpl implements ClientHrWrapper {

	@Override
	public void setValuesToClientHrDto(ClientHrDto dto) {
		if(dto.getHrScopName()==null) {
			dto.setHrScopName("NA");
		}
		if(dto.getHrEmail()==null) {
			dto.setHrEmail("NA");
		}
		if(dto.getHrContactNumber()==null) {
			dto.setHrContactNumber(0l);
		}
		if(dto.getDesignation()==null) {
			dto.setDesignation("NA");
		}
		if(dto.getStatus()==null) {
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
	public ClientHrDto listToClientHrDto(List<Object> row) {
    ClientHrDto dto	=new ClientHrDto();
    int rowSize = row.size();
	// Set clientDto properties based on the elements in the input list
	if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
		dto.setId(Integer.valueOf(row.get(0).toString()));
	}
	if (rowSize > 1 && row.get(1) != null && !row.get(1).toString().isEmpty()) {
		dto.setCompanyId(Integer.valueOf(row.get(1).toString()));
	}
	if (rowSize > 2 && row.get(2) != null && !row.get(2).toString().isEmpty()) {
		dto.setHrScopName((String) row.get(2));
	}
	if (rowSize > 3 && row.get(3) != null && !row.get(3).toString().isEmpty()) {
		dto.setHrEmail((String)row.get(3));
	}

	if (rowSize > 4 && row.get(4) != null && !row.get(4).toString().isEmpty()) {
		dto.setHrContactNumber(Long.parseLong(row.get(4).toString()));
	}
	if (rowSize > 5 && row.get(5) != null && !row.get(5).toString().isEmpty()) {
		dto.setDesignation((String)row.get(5).toString());
	}

	if (rowSize > 6 && row.get(6) != null && !row.get(6).toString().isEmpty()) {
		dto.setStatus((String) row.get(6));
	}
	if (rowSize > 7 && row.get(7) != null && !row.get(7).toString().isEmpty()) {
		if (dto.getAdminDto() == null) {
			dto.setAdminDto(new AuditDto());
		}
		dto.getAdminDto().setCreatedBy((String) row.get(7));
	}
	if (rowSize > 8 && row.get(8) != null && !row.get(8).toString().isEmpty()) {
		if (dto.getAdminDto() == null) {
			dto.setAdminDto(new AuditDto());
		}
		dto.getAdminDto().setCreatedOn((String) row.get(8));
	}
	if (rowSize > 9 && row.get(9) != null && !row.get(9).toString().isEmpty()) {
		if (dto.getAdminDto() == null) {
			dto.setAdminDto(new AuditDto());
		}
		dto.getAdminDto().setUpdatedBy((String) row.get(9));
	}
	if (rowSize > 10 && row.get(10) != null && !row.get(10).toString().isEmpty()) {
		if (dto.getAdminDto() == null) {
			dto.setAdminDto(new AuditDto());
		}
		dto.getAdminDto().setUpdatedOn((String) row.get(10));
	}
	//log.info("clinet wrapper assigning list value to the dto {}", clientDto);
	return dto;
	}

}
