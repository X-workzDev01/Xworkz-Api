package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.AbsentDaysDto;
import com.xworkz.dream.dto.AbsentDto;
import com.xworkz.dream.dto.AbsenteesDto;
import com.xworkz.dream.dto.AttendanceDataDto;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.AttendanceTrainee;
import com.xworkz.dream.service.AttendanceService;
import com.xworkz.dream.service.BatchAttendanceService;
import com.xworkz.dream.service.BatchService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	@Autowired
	private BatchService batchService;
	@Autowired
	private BatchAttendanceService batchAttendanceService;
	@Value("${login.sheetId}")
	private String spreadsheetId;
	private static final Logger log = LoggerFactory.getLogger(AttendanceController.class);

	@ApiOperation(value = "To register attendance details in the google sheets")
	@PostMapping("/register")
	public ResponseEntity<String> registerAttendance(@RequestBody AttendanceDto values, HttpServletRequest request) {
		ResponseEntity<String> response = attendanceService.writeAttendance(spreadsheetId, values, request);
		return response;
	}

	@ApiOperation(value = "Everyday mark absentees by batch")
	@PostMapping("/absentees")
	public List<String> markAttendance(@RequestBody List<AbsenteesDto> absentDtoList, @RequestParam String batch) {
		List<String> markAndSaveAbsentDetails = attendanceService.markAndSaveAbsentDetails(absentDtoList, batch);
		if (markAndSaveAbsentDetails != null) {
			return markAndSaveAbsentDetails;
		}
		return null;
 
	}

	@ApiOperation(value = "Get trainees joined by batch")
	@GetMapping("/trainee")
	public List<AttendanceTrainee> getAttendanceTrainee(@RequestParam String batch) {
		List<AttendanceTrainee> trainee = attendanceService.getTrainee(batch);
		return trainee;
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<AbsentDto> getAbsentDetails(@PathVariable Integer id, @RequestParam String batch)
			throws IOException {
		List<AbsentDaysDto> listOfAbsentDays = attendanceService.getAttendanceById(id);
		Integer gettotalClassByCourseName = batchService.gettotalClassByCourseName(batch);
		AbsentDto absentDto = new AbsentDto();
		absentDto.setList(listOfAbsentDays);
		absentDto.setTotalClass(gettotalClassByCourseName);
		ResponseEntity<AbsentDto> responseEntity = new ResponseEntity<AbsentDto>(absentDto, HttpStatus.OK);
		return responseEntity;
	}

	@GetMapping("/batch")
	public ResponseEntity<List<AttendanceDto>> getAbsentDataByBatch(@RequestParam String batch) {
		List<AttendanceDto> attendanceByBatch = attendanceService.getAbsentListByBatch(batch);
		ResponseEntity<List<AttendanceDto>> attendanceList = new ResponseEntity<List<AttendanceDto>>(attendanceByBatch,
				HttpStatus.OK);
		return attendanceList;
	}

	@PostMapping("/batchAttendance")
	public String markBatchAttendance(@RequestParam String courseName, @RequestParam Boolean batchAttendanceStatus) {
		Boolean markTraineeAttendance = attendanceService.markTrainerAttendance(courseName, batchAttendanceStatus);
		if (markTraineeAttendance == true) {
			return "Batch Attendance Update successfully";
		} else {
			return "Batch Attendance Already Update";
		}
	}

	@PostMapping("/addTrainee")
	public ResponseEntity<Map<Integer, String>> addTraineeToJoined(@RequestParam String courseName) {
		List<AttendanceDto> addJoined = attendanceService.addJoined(courseName);
		Map<Integer, String> traineeNameAndCourseNameMap = new HashMap<>();
		for (AttendanceDto attendanceDto : addJoined) {
			traineeNameAndCourseNameMap.put(attendanceDto.getId(), attendanceDto.getTraineeName());
		}
		return new ResponseEntity<>(traineeNameAndCourseNameMap, HttpStatus.OK);
	}

	@GetMapping("/readData")
	public ResponseEntity<AttendanceDataDto> readData(@RequestParam Integer startingIndex,
			@RequestParam Integer maxRows, @RequestParam String courseName) {
		log.info("Reading data with parameters - SpreadsheetId: {}, Starting Index: {}, Max Rows: {}, Course Name: {}",
				spreadsheetId, startingIndex, maxRows, courseName);
		return attendanceService.attendanceReadData(startingIndex, maxRows, courseName);
	}

	@GetMapping("/filterData/{courseName}")
	public List<AttendanceDto> filterData(@PathVariable String courseName, @RequestParam String searchValue) {

		log.info("Filtering data with parameters - SpreadsheetId: {}, Search Value: {}", spreadsheetId, searchValue);
		return attendanceService.filterData(searchValue, courseName);

	}

	@GetMapping("/suggestion/{courseName}")
	public ResponseEntity<List<AttendanceDto>> getSearchSuggestion(@RequestParam String value,
			@PathVariable String courseName) {
		log.info("Getting suggestions for search: {}", value);
		return attendanceService.getSearchSuggestion(value, courseName);

	}
	
	@GetMapping("/getBatchAttendanceCount")
	public Map<Integer, Boolean> getBatchAttendanceCount(@RequestParam String courseName){
		Map<Integer, Boolean> batchAttendanceDetails = batchAttendanceService.getBatchAttendanceDetails(courseName);
		return batchAttendanceDetails;
	}

}
