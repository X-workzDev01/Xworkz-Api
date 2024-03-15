package com.xworkz.dream.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.BirthdayDataDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.BirthadayService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class BirthdayController {
	@Autowired
	private BirthadayService service;
	private Logger log = LoggerFactory.getLogger(BirthdayController.class);

	@ApiOperation(value = "To update Birth day info while registering")
	@PostMapping("/birthDayInfo")
	public ResponseEntity<String> saveBirthDayInfo(@RequestBody TraineeDto dto) {
		log.info("Saving Birthday details into birthday sheet");
		String response=this.service.saveBirthDayInfo( dto);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "To get Birth day info with pagination")
	@GetMapping("/birthdays")
	public ResponseEntity<BirthdayDataDto> getBirthDayInfo(@RequestHeader String spreadsheetId,
			@RequestParam int startingIndex, @RequestParam int maxRows, @RequestParam String date,
			@RequestParam String courseName) throws IllegalAccessException, IOException {
		log.info("Request received for get birthdays. SpreadsheetId: {}", spreadsheetId);
		return ResponseEntity.ok(service.getBirthdays(spreadsheetId, startingIndex, maxRows, date, courseName));
	}

}
