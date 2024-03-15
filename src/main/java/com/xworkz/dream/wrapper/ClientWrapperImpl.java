package com.xworkz.dream.wrapper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xworkz.dream.clientDtos.ClientValueDto;
import com.xworkz.dream.constants.ClientConstant;
import com.xworkz.dream.constants.ClientFollowUpConstant;
import com.xworkz.dream.constants.ClientHrConstant;
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.HrFollowUpDto;

@Service
public class ClientWrapperImpl implements ClientWrapper {
	private static final Logger log = LoggerFactory.getLogger(ClientWrapper.class);

	public static boolean validateCell(ClientConstant clientConstant) {
		return StringUtils.hasLength(String.valueOf(clientConstant.getIndex()));
	}

	public static boolean validateCell(ClientHrConstant clientHrConstant) {
		return StringUtils.hasLength(String.valueOf(clientHrConstant.getIndex()));
	}

	public static boolean validateCell(ClientFollowUpConstant clientFollowUpConstant) {
		return StringUtils.hasLength(String.valueOf(clientFollowUpConstant.getIndex()));
	}

	@Override
	public ClientDto listToClientDto(List<Object> row) {
		ClientDto clientDto = new ClientDto();
		int rowSize = row.size();
		log.info("mapping to dto, size of the data:{}", rowSize);
		if (rowSize > 1) {
			if (rowSize > ClientConstant.COLUMN_ID.getIndex() && validateCell(ClientConstant.COLUMN_ID)) {
				clientDto.setId(Integer.valueOf(row.get(ClientConstant.COLUMN_ID.getIndex()).toString()));
			}
			if (rowSize > ClientConstant.COLUMN_COMPANY_NAME.getIndex()
					&& validateCell(ClientConstant.COLUMN_COMPANY_NAME)) {
				clientDto.setCompanyName(row.get(ClientConstant.COLUMN_COMPANY_NAME.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_EMAIL.getIndex() && validateCell(ClientConstant.COLUMN_EMAIL)) {
				clientDto.setCompanyEmail(row.get(ClientConstant.COLUMN_EMAIL.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_LANDLINE_NUMBER.getIndex()
					&& validateCell(ClientConstant.COLUMN_LANDLINE_NUMBER)) {
				clientDto.setCompanyLandLineNumber(
						Long.valueOf(row.get(ClientConstant.COLUMN_LANDLINE_NUMBER.getIndex()).toString()));
			}
			if (rowSize > ClientConstant.COLUMN_WEBSITE.getIndex() && validateCell(ClientConstant.COLUMN_WEBSITE)) {
				clientDto.setCompanyWebsite(row.get(ClientConstant.COLUMN_WEBSITE.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_LOCATION.getIndex() && validateCell(ClientConstant.COLUMN_LOCATION)) {
				clientDto.setCompanyLocation(row.get(ClientConstant.COLUMN_LOCATION.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_FOUNDER.getIndex() && validateCell(ClientConstant.COLUMN_FOUNDER)) {
				clientDto.setCompanyFounder(row.get(ClientConstant.COLUMN_FOUNDER.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_SOURCE_OF_CONNECTION.getIndex()
					&& validateCell(ClientConstant.COLUMN_SOURCE_OF_CONNECTION)) {
				clientDto.setSourceOfConnection(
						row.get(ClientConstant.COLUMN_SOURCE_OF_CONNECTION.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_COMPANY_TYPE.getIndex()
					&& validateCell(ClientConstant.COLUMN_COMPANY_TYPE)) {
				clientDto.setCompanyType(row.get(ClientConstant.COLUMN_COMPANY_TYPE.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_COMPANY_TYPE.getIndex()
					&& validateCell(ClientConstant.COLUMN_COMPANY_TYPE)) {
				clientDto.setCompanyType(row.get(ClientConstant.COLUMN_COMPANY_TYPE.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_ADDRESS.getIndex() && validateCell(ClientConstant.COLUMN_ADDRESS)) {
				clientDto.setCompanyAddress(row.get(ClientConstant.COLUMN_ADDRESS.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_STATUS.getIndex() && validateCell(ClientConstant.COLUMN_STATUS)) {
				clientDto.setStatus(row.get(ClientConstant.COLUMN_STATUS.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_CREATED_BY.getIndex()
					&& validateCell(ClientConstant.COLUMN_CREATED_BY)) {
				if (clientDto.getAdminDto() == null) {
					clientDto.setAdminDto(new AuditDto());
				}
				clientDto.getAdminDto().setCreatedBy(row.get(ClientConstant.COLUMN_CREATED_BY.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_CREATED_ON.getIndex()
					&& validateCell(ClientConstant.COLUMN_CREATED_ON)) {
				if (clientDto.getAdminDto() == null) {
					clientDto.setAdminDto(new AuditDto());
				}
				clientDto.getAdminDto().setCreatedOn(row.get(ClientConstant.COLUMN_CREATED_ON.getIndex()).toString());
			}
			if (rowSize > ClientConstant.COLUMN_UPDATED_BY.getIndex()
					&& validateCell(ClientConstant.COLUMN_UPDATED_BY)) {
				if (clientDto.getAdminDto() == null) {
					clientDto.setAdminDto(new AuditDto());
					clientDto.getAdminDto()
							.setUpdatedBy(row.get(ClientConstant.COLUMN_UPDATED_BY.getIndex()).toString());
				}

			}
			if (rowSize > ClientConstant.COLUMN_UPDATED_ON.getIndex()
					&& validateCell(ClientConstant.COLUMN_UPDATED_ON)) {
				if (clientDto.getAdminDto() == null) {
					clientDto.setAdminDto(new AuditDto());
					clientDto.getAdminDto()
							.setUpdatedOn(row.get(ClientConstant.COLUMN_UPDATED_ON.getIndex()).toString());
				}

			}
		}
		return clientDto;
	}

	@Override
	public HrFollowUpDto listToHrFollowUpDto(List<Object> row) {

		HrFollowUpDto dto = new HrFollowUpDto();
		if (row == null) {
			return dto;
		} else {
			int rowSize = row.size();
			if (rowSize > 1) {
				if (rowSize > ClientFollowUpConstant.COLUMN_ID.getIndex()
						&& validateCell(ClientFollowUpConstant.COLUMN_ID)) {
					dto.setId(Integer.valueOf(row.get(ClientFollowUpConstant.COLUMN_ID.getIndex()).toString()));
				}
				if (rowSize > ClientFollowUpConstant.COLUMN_HR_ID.getIndex()
						&& validateCell(ClientFollowUpConstant.COLUMN_HR_ID)) {
					dto.setHrId(Integer.valueOf(row.get(ClientFollowUpConstant.COLUMN_HR_ID.getIndex()).toString()));
				}
				if (rowSize > ClientFollowUpConstant.COLUMN_ATTEMPT_ON.getIndex()
						&& validateCell(ClientFollowUpConstant.COLUMN_ATTEMPT_ON)) {
					dto.setAttemptOn(row.get(ClientFollowUpConstant.COLUMN_ATTEMPT_ON.getIndex()).toString());
				}
				if (rowSize > ClientFollowUpConstant.COLUMN_ATTEMPTED_BY.getIndex()
						&& validateCell(ClientFollowUpConstant.COLUMN_ATTEMPTED_BY)) {
					dto.setAttemptBy(row.get(ClientFollowUpConstant.COLUMN_ATTEMPTED_BY.getIndex()).toString());
				}
				if (rowSize > ClientFollowUpConstant.COLUMN_ATTEMPT_STATUS.getIndex()
						&& validateCell(ClientFollowUpConstant.COLUMN_ATTEMPT_STATUS)) {
					dto.setAttemptStatus(row.get(ClientFollowUpConstant.COLUMN_ATTEMPT_STATUS.getIndex()).toString());
				}
				if (rowSize > ClientFollowUpConstant.COLUMN_CALL_DURATION.getIndex()
						&& validateCell(ClientFollowUpConstant.COLUMN_CALL_DURATION)) {
					dto.setCallDuration(row.get(ClientFollowUpConstant.COLUMN_CALL_DURATION.getIndex()).toString());
				}
				if (rowSize > ClientFollowUpConstant.COLUMN_CALL_BACK_DATE.getIndex()
						&& validateCell(ClientFollowUpConstant.COLUMN_CALL_BACK_DATE)) {
					dto.setCallBackDate(row.get(ClientFollowUpConstant.COLUMN_CALL_BACK_DATE.getIndex()).toString());
				}
				if (rowSize > ClientFollowUpConstant.COLUMN_CALL_BACK_TIME.getIndex()
						&& validateCell(ClientFollowUpConstant.COLUMN_CALL_BACK_TIME)) {
					dto.setCallBackTime(row.get(ClientFollowUpConstant.COLUMN_CALL_BACK_TIME.getIndex()).toString());
				}
				if (rowSize > ClientFollowUpConstant.COLUMN_COMMENTS.getIndex()
						&& validateCell(ClientFollowUpConstant.COLUMN_COMMENTS)) {
					dto.setComments(row.get(ClientFollowUpConstant.COLUMN_COMMENTS.getIndex()).toString());
				}
				return dto;
			}
		}
		return dto;
	}

	@Override
	public ClientHrDto listToClientHrDto(List<Object> row) {
		ClientHrDto dto = new ClientHrDto();
		if (row == null) {
			return dto;
		} else {
			int rowSize = row.size();
			if (rowSize > 1) {
				if (rowSize > ClientHrConstant.COLUMN_ID.getIndex() && validateCell(ClientHrConstant.COLUMN_ID)) {
					dto.setId(Integer.valueOf(row.get(0).toString()));
				}
				if (rowSize > ClientHrConstant.COLUMN_COMPANY_ID.getIndex()
						&& validateCell(ClientHrConstant.COLUMN_COMPANY_ID)) {
					dto.setCompanyId(
							Integer.valueOf(row.get(ClientHrConstant.COLUMN_COMPANY_ID.getIndex()).toString()));
				}
				if (rowSize > ClientHrConstant.COLUMN_SPOCNAME.getIndex()
						&& validateCell(ClientHrConstant.COLUMN_SPOCNAME)) {
					dto.setHrSpocName((String) row.get(ClientHrConstant.COLUMN_SPOCNAME.getIndex()));
				}
				if (rowSize > ClientHrConstant.COLUMN_HR_EMAIL.getIndex()
						&& validateCell(ClientHrConstant.COLUMN_HR_EMAIL)) {
					dto.setHrEmail((String) row.get(ClientHrConstant.COLUMN_HR_EMAIL.getIndex()));
				}
				if (rowSize > ClientHrConstant.COLUMN_CONTACT_NUMBER.getIndex()
						&& validateCell(ClientHrConstant.COLUMN_CONTACT_NUMBER)) {
					dto.setHrContactNumber(
							Long.parseLong(row.get(ClientHrConstant.COLUMN_CONTACT_NUMBER.getIndex()).toString()));
				}
				if (rowSize > ClientHrConstant.COLUMN_DESIGNATION.getIndex()
						&& validateCell(ClientHrConstant.COLUMN_DESIGNATION)) {
					dto.setDesignation((String) row.get(ClientHrConstant.COLUMN_DESIGNATION.getIndex()));
				}
				if (rowSize > ClientHrConstant.COLUMN_COMMENTS.getIndex()
						&& validateCell(ClientHrConstant.COLUMN_COMMENTS)) {
					dto.setStatus((String) row.get(ClientHrConstant.COLUMN_COMMENTS.getIndex()));
				}
				if (rowSize > ClientHrConstant.COLUMN_CREATED_BY.getIndex()
						&& validateCell(ClientHrConstant.COLUMN_CREATED_BY)) {
					if (dto.getAdminDto() == null) {
						dto.setAdminDto(new AuditDto());
					}
					dto.getAdminDto().setCreatedBy((String) row.get(ClientHrConstant.COLUMN_CREATED_BY.getIndex()));
				}
				if (rowSize > ClientHrConstant.COLUMN_CREATED_ON.getIndex()
						&& validateCell(ClientHrConstant.COLUMN_CREATED_ON)) {
					if (dto.getAdminDto() == null) {
						dto.setAdminDto(new AuditDto());
					}
					dto.getAdminDto().setCreatedOn((String) row.get(ClientHrConstant.COLUMN_CREATED_ON.getIndex()));
				}
				if (rowSize > ClientHrConstant.COLUMN_UPDATED_BY.getIndex()
						&& validateCell(ClientHrConstant.COLUMN_UPDATED_BY)) {
					if (dto.getAdminDto() == null) {
						dto.setAdminDto(new AuditDto());
					}
					dto.getAdminDto().setUpdatedBy((String) row.get(ClientHrConstant.COLUMN_UPDATED_BY.getIndex()));
				}
				if (rowSize > ClientHrConstant.COLUMN_UPDATED_ON.getIndex()
						&& validateCell(ClientHrConstant.COLUMN_UPDATED_ON)) {
					if (dto.getAdminDto() == null) {
						dto.setAdminDto(new AuditDto());
					}
					dto.getAdminDto().setUpdatedOn((String) row.get(ClientHrConstant.COLUMN_UPDATED_ON.getIndex()));
				}
				return dto;
			}
		}
		return dto;
	}

	@Override
	public ClientValueDto listToClientValueDto(List<Object> row) {
		int rowSize = row.size();
		ClientValueDto clientValueDto = new ClientValueDto();
		if (rowSize > 0 && row.get(0) != null && !row.get(0).toString().isEmpty()) {
			clientValueDto.setMapValue(row.get(0).toString());
		}
		return clientValueDto;
	}
}
