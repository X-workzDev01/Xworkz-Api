package com.xworkz.dream.wrapper;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.HrFollowUpDto;

@Service
public class ClientWrapperImpl implements ClientWrapper {
	private static final Logger log = LoggerFactory.getLogger(ClientWrapper.class);

	@Override
	public ClientDto listToClientDto(List<Object> row) {

		ClientDto clientDto = new ClientDto();
		int rowSize = row.size();
		if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
			clientDto.setId(Integer.valueOf(row.get(0).toString()));
		}
		if (rowSize > 1 && row.get(1) != null && !row.get(1).toString().isEmpty()) {
			clientDto.setCompanyName((String) row.get(1));
		}
		if (rowSize > 2 && row.get(2) != null && !row.get(2).toString().isEmpty()) {
			clientDto.setCompanyEmail((String) row.get(2));
		}
		if (rowSize > 3 && row.get(3) != null && !row.get(3).toString().isEmpty()) {
			clientDto.setCompanyLandLineNumber(Long.parseLong(row.get(3).toString()));
		}

		if (rowSize > 4 && row.get(4) != null && !row.get(4).toString().isEmpty()) {
			clientDto.setCompanyWebsite((String) row.get(4));
		}
		if (rowSize > 5 && row.get(5) != null && !row.get(5).toString().isEmpty()) {
			clientDto.setCompanyLocation((String) row.get(5).toString());
		}

		if (rowSize > 6 && row.get(6) != null && !row.get(6).toString().isEmpty()) {
			clientDto.setCompanyFounder((String) row.get(6).toString());
		}
		if (rowSize > 7 && row.get(7) != null && !row.get(7).toString().isEmpty()) {
			clientDto.setSourceOfConnection((String) row.get(7).toString());
		}
		if (rowSize > 8 && row.get(8) != null && !row.get(8).toString().isEmpty()) {
			clientDto.setCompanyType((String) row.get(8).toString());
		}
		if (rowSize > 9 && row.get(9) != null && !row.get(9).toString().isEmpty()) {
			clientDto.setCompanyAddress((String) row.get(9).toString());
		}

		if (rowSize > 10 && row.get(10) != null && !row.get(10).toString().isEmpty()) {
			clientDto.setStatus((String) row.get(10));
		}
		if (rowSize > 11 && row.get(11) != null && !row.get(11).toString().isEmpty()) {
			if (clientDto.getAdminDto() == null) {
				clientDto.setAdminDto(new AuditDto());
			}
			clientDto.getAdminDto().setCreatedBy((String) row.get(11));
		}
		if (rowSize > 12 && row.get(12) != null && !row.get(12).toString().isEmpty()) {
			if (clientDto.getAdminDto() == null) {
				clientDto.setAdminDto(new AuditDto());
			}
			clientDto.getAdminDto().setCreatedOn((String) row.get(12));
		}
		if (rowSize > 13 && row.get(13) != null && !row.get(13).toString().isEmpty()) {
			if (clientDto.getAdminDto() == null) {
				clientDto.setAdminDto(new AuditDto());
			}
			clientDto.getAdminDto().setUpdatedBy((String) row.get(13));
		}
		if (rowSize > 14 && row.get(14) != null && !row.get(14).toString().isEmpty()) {
			if (clientDto.getAdminDto() == null) {
				clientDto.setAdminDto(new AuditDto());
			}
			clientDto.getAdminDto().setUpdatedOn((String) row.get(14));
		}
		log.debug("clinet wrapper assigning list value to the dto {}", clientDto);
		return clientDto;
	}

	@Override
	public HrFollowUpDto listToHrFollowUpDto(List<Object> row) {

		HrFollowUpDto dto = new HrFollowUpDto();
		int rowSize = row.size();
		// Set clientDto properties based on the elements in the input list
		if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
			dto.setId(Integer.valueOf(row.get(0).toString()));
		}
		if (rowSize > 1 && row.get(1) != null && !row.get(1).toString().isEmpty()) {
			dto.setHrId(Integer.valueOf(row.get(1).toString()));
		}
		if (rowSize > 2 && row.get(2) != null && !row.get(2).toString().isEmpty()) {
			dto.setAttemptOn(row.get(2).toString());
		}
		if (rowSize > 3 && row.get(3) != null && !row.get(3).toString().isEmpty()) {
			dto.setAttemptBy(row.get(3).toString());
		}
		if (rowSize > 4 && row.get(4) != null && !row.get(4).toString().isEmpty()) {
			dto.setAttemptStatus(row.get(4).toString());
		}
		if (rowSize > 5 && row.get(5) != null && !row.get(5).toString().isEmpty()) {
			dto.setCallDuration(Integer.valueOf(row.get(5).toString()));
		}
		if (rowSize > 6 && row.get(6) != null && !row.get(6).toString().isEmpty()) {
			dto.setCallBackDate(row.get(6).toString());
		}
		if (rowSize > 7 && row.get(7) != null && !row.get(7).toString().isEmpty()) {
			dto.setCallBackTime(row.get(7).toString());
		}
		if (rowSize > 8 && row.get(8) != null && !row.get(8).toString().isEmpty()) {
			dto.setComments(row.get(8).toString());
		}
		return dto;
	}

	@Override
	public ClientHrDto listToClientHrDto(List<Object> row) {
		ClientHrDto dto = new ClientHrDto();
		int rowSize = row.size();
		// Set clientDto properties based on the elements in the input list
		if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
			dto.setId(Integer.valueOf(row.get(0).toString()));
		}
		if (rowSize > 1 && row.get(1) != null && !row.get(1).toString().isEmpty()) {
			dto.setCompanyId(Integer.valueOf(row.get(1).toString()));
		}
		if (rowSize > 2 && row.get(2) != null && !row.get(2).toString().isEmpty()) {
			dto.setHrSpocName((String) row.get(2));
		}
		if (rowSize > 3 && row.get(3) != null && !row.get(3).toString().isEmpty()) {
			dto.setHrEmail((String) row.get(3));
		}

		if (rowSize > 4 && row.get(4) != null && !row.get(4).toString().isEmpty()) {
			dto.setHrContactNumber(Long.parseLong(row.get(4).toString()));
		}
		if (rowSize > 5 && row.get(5) != null && !row.get(5).toString().isEmpty()) {
			dto.setDesignation((String) row.get(5).toString());
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
		// log.info("clinet wrapper assigning list value to the dto {}", clientDto);
		return dto;
	}

}
