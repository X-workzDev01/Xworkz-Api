package com.xworkz.dream.wrapper;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.HrFollowUpDto;
@Service
public class HrFollowUpWrapperImpl implements HrFollowUpWrapper {

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

}
