package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.AbsentDaysDto;
import com.xworkz.dream.dto.AbsentDto;
import com.xworkz.dream.dto.AbsenteesDto;
import com.xworkz.dream.dto.AttendanceDataDto;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.AttendanceTrainee;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.AttendanceService;
import com.xworkz.dream.service.BatchService;

import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	@Autowired
	private BatchService batchService;
	@Value("${login.sheetId}")
	private String spreadsheetId;
	private static final Logger log = LoggerFactory.getLogger(AttendanceController.class);

	@ApiOperation(value = "To register attendance details in the google sheets")
	@PostMapping("/register")
	public ResponseEntity<String> registerAttendance(@RequestBody AttendanceDto values, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException, IllegalAccessException {
		log.info("Received request to register attendance.");
		ResponseEntity<String> response = attendanceService.writeAttendance(spreadsheetId, values, request);

		log.info("Attendance registration completed.");
		return response;
	}

	@ApiOperation(value = "Everyday mark absentees by batch")
	@PostMapping("/absentees")
	public void markAttendance(@RequestBody List<AbsenteesDto> absentDtoList, @RequestParam String batch)
			throws IOException, IllegalAccessException {
		log.info("Received request to mark attendance for multiple users.");
		attendanceService.markAndSaveAbsentDetails(absentDtoList, batch);
		log.info("Attendance marking completed.");

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
	public ResponseEntity<List<AttendanceDto>> getAbsentDataByBatch(@RequestParam String batch) throws IOException {
		List<AttendanceDto> attendanceByBatch = attendanceService.getAbsentListByBatch(batch);
		ResponseEntity<List<AttendanceDto>> attendanceList = new ResponseEntity<List<AttendanceDto>>(attendanceByBatch,
				HttpStatus.OK);
		return attendanceList;
	}

	@PostMapping("/batchAttendance")
	public String markBatchAttendance(@RequestParam String courseName, @RequestParam Boolean batchAttendanceStatus)
			throws IOException, IllegalAccessException {
		Boolean markTraineeAttendance = attendanceService.markTraineeAttendance(courseName, batchAttendanceStatus);
		if (markTraineeAttendance == true) {
			return "Batch Attendance Update successfully";
		} else {
			return "Batch Attendance Already Update";
		}
	}

	@PostMapping("/addTrainee")
	public ResponseEntity<Map<Integer, String>> addTraineeToJoind(@RequestParam String courseName)
			throws IOException, IllegalAccessException {
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
	public List<AttendanceDto> filterData(@PathVariable String courseName,
			@RequestParam String searchValue) {
		try {
			log.info("Filtering data with parameters - SpreadsheetId: {}, Search Value: {}", spreadsheetId,
					searchValue);
			return attendanceService.filterData(searchValue, courseName);
		} catch (IOException e) {
			log.error("An error occurred during data filtering", e.getMessage());
		}
		return null;

	}

	@GetMapping("/suggestion/{courseName}")
	public ResponseEntity<List<AttendanceDto>> getSearchSuggestion(@RequestParam String value, @PathVariable String courseName) {
		log.info("Getting suggestions for search: {}", value);
		return attendanceService.getSearchSuggestion(value, courseName);

	}

}
