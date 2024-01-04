package com.xworkz.dream.resource;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.AbsentDaysDto;
import com.xworkz.dream.dto.AbsentDto;
import com.xworkz.dream.dto.AbsenteesDto;
import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.AttendanceTrainee;
import com.xworkz.dream.service.AttendanceService;

import freemarker.template.TemplateException;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
	@Autowired
	private AttendanceService attendanceService;
	@Value("${login.sheetId}")
	private String spreadsheetId;
	private static final Logger log = LoggerFactory.getLogger(AttendanceController.class);

	@ApiOperation(value = "To register attendance details in the google sheets")
	@PostMapping("/register")
	public ResponseEntity<String> registerAttendance(@RequestBody AttendanceDto values, HttpServletRequest request)
			throws IOException, MessagingException, TemplateException {
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

	@GetMapping("/id")
	public ResponseEntity<AbsentDto> getAbsentDetails(@RequestParam Integer id) {
		List<AbsentDaysDto> listOfAbsentDays = attendanceService.getAttendanceById(id);
		AbsentDto absentDto = new AbsentDto();
		absentDto.setList(listOfAbsentDays);
		absentDto.setTotalClass(30);
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

}
