package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.aspectj.weaver.patterns.HasMemberTypePatternForPerThisMatching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.BatchAttendanceDto;
import com.xworkz.dream.dto.utils.WrapperUtil;
import com.xworkz.dream.repository.BatchAttendanceRepository;
import com.xworkz.dream.wrapper.BatchWrapper;

@Service
public class BatchAttendanceServiceImpl implements BatchAttendanceService {
	@Value("${login.sheetId}")
	private String sheetId;
	@Autowired
	@Value("${sheets.batchAttendanceInfoRange}")
	private String batchAttendanceInfoRange;
	@Value("${sheets.getBatchAttendanceInfoRange}")
	private String getBatchAttendanceInfoRange;
	@Value("${sheets.batchAttendanceInfoSheetName}")
	private String batchAttendanceInfoSheetName;
	@Autowired
	private BatchAttendanceRepository repository;
	@Autowired
    private WrapperUtil util;
	

	@Override
	public ResponseEntity<String> writeBatchAttendance(BatchAttendanceDto batchAttendanceDto)
			throws IOException, IllegalAccessException {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Set<ConstraintViolation<BatchAttendanceDto>> violation = factory.getValidator().validate(batchAttendanceDto);
		if (violation.isEmpty() && batchAttendanceDto != null) {
			List<Object> extractDtoDetails = util.extractDtoDetails(batchAttendanceDto);
			repository.batchAttendance(extractDtoDetails, batchAttendanceInfoRange);
			return ResponseEntity.ok("Batch Attendance Detiles Added Sucessfully");

		}

		return ResponseEntity.ok("Batch Attendance detiles does not added ");

	}

	@Override
	public Boolean getPresentDate(String courseName) throws IOException {
		List<List<Object>> batchAttendanceData = repository.getBatchAttendanceData(getBatchAttendanceInfoRange);
		if (batchAttendanceData != null && !batchAttendanceData.toString().contains("#NUM!")) {
			List<String> presentDates = batchAttendanceData.stream()
					.filter(batchAttendance -> courseName.equals(batchAttendance.get(1)))
					.map(batchAttendance -> (String) batchAttendance.get(3)).collect(Collectors.toList());
			for (String date : presentDates) {
				if (date != null && date.equals(LocalDate.now().toString())) {
					return true;
				}
			}

		}
		return false;
	}

	@Override
	public Map<Integer, Boolean> getBatchAttendanceDetails(String courseName) {
		Map<Integer, Boolean> attendanceDetails = new HashMap<Integer, Boolean>();
		try {
			List<List<Object>> batchAttendanceData = repository.getBatchAttendanceData(getBatchAttendanceInfoRange);
			if (batchAttendanceData != null && !batchAttendanceData.isEmpty() && !batchAttendanceData.toString().contains("#NUM!")) {
				Integer totalAttendanceForCourse = (int) batchAttendanceData.stream()
						.filter(attendanceRecord -> attendanceRecord.get(1).equals(courseName)).count();
				Boolean isTodayPresent = batchAttendanceData.stream()
						.filter(attendanceRecord -> attendanceRecord.get(1).toString().equalsIgnoreCase(courseName))
						.map(attendanceRecord -> attendanceRecord.get(3))
						.anyMatch(attendanceDate -> attendanceDate.equals(LocalDate.now().toString()));
				attendanceDetails.put(totalAttendanceForCourse, isTodayPresent);

				return attendanceDetails;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return attendanceDetails;

	}

}
