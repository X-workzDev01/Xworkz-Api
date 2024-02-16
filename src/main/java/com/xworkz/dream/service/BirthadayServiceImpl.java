package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.BirthadayRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service

public class BirthadayServiceImpl implements BirthadayService {

	@Autowired
	private BirthadayRepository repository;
	@Autowired
	private DreamUtil util;
	@Autowired
	private DreamWrapper wrapper;
	@Value("${login.sheetId}")
	private String spreadsheetId;
	@Value("${sheets.birthDayStartRow}")
	private String birthDayStartRow;
	@Value("${sheets.birthDayEndRow}")
	private String birthDayEndRow;
	@Value("${sheets.dateOfBirthDetailsSheetName}")
	private String dobSheetName;

	private static final Logger log = LoggerFactory.getLogger(BirthadayServiceImpl.class);

	@Override
	public ResponseEntity<String> saveBirthDayInfo(String spreadsheetId, TraineeDto dto)
			throws IllegalAccessException, IOException {
		BirthDayInfoDto birthday = assignToBirthDayDto(dto);
		List<Object> list = wrapper.extractDtoDetails(birthday);

		boolean save = repository.saveBirthDayDetails(spreadsheetId, list);
		if (save != false) {
			log.info("Birth day information added successfully");
			return ResponseEntity.ok("Birth day information added successfully");
		}
		log.info("Birth day information not added");
		return ResponseEntity.ok("Birth day information Not added");
	}

	private BirthDayInfoDto assignToBirthDayDto(TraineeDto dto) {
		BirthDayInfoDto birthday = new BirthDayInfoDto();
		birthday.setDto(dto.getBasicInfo());
		birthday.setBirthDayMailSent("NO");
		birthday.setAuditDto(dto.getAdminDto());
		return birthday;
	}

	private String findNameByEmail(String email) throws IOException {
		List<List<Object>> birthdayDetails = repository.getBirthadayDetails(spreadsheetId);
		Optional<String> optionalName = birthdayDetails.stream().filter(row -> email.equals(row.get(2)))
				.map(row -> (String) row.get(1)).findFirst();

		String name = optionalName.orElse("Unknown");
		log.debug("Found name '{}' for email '{}'", name, email);
		return name;

	}

	@Override
	public void sendBirthdayEmails() throws IOException {
		String subject = "Birthday Wishes : X-workZ";
		List<List<Object>> birthdayDetails = repository.getBirthadayDetails(spreadsheetId);
		log.debug("Birthday details: {}", birthdayDetails);
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<String> emailsToSend = birthdayDetails.stream().filter(row -> {
			if (row.size() < 5) {
				log.warn("Row does not have enough columns: {}", row);
				return false;
			}
			String birthday = (String) row.get(4);
			if ("NA".equals(birthday)) {
				return false; // Ignore rows with "NA" birthday
			}
			try {
				LocalDate dob = LocalDate.parse((String) row.get(4), dateFormatter);
				return dob.getMonth() == currentDate.getMonth() && dob.getDayOfMonth() == currentDate.getDayOfMonth();
			} catch (DateTimeParseException e) {
				log.warn("Error parsing birthday for row: {}", row);
				return false; // Skip rows with invalid date format
			}
		}).map(row -> (String) row.get(2)).collect(Collectors.toList());
		for (String email : emailsToSend) {
			String nameByEmail = findNameByEmail(email);
			log.info("Sent birthday email to '{}' with name '{}'", email, nameByEmail);
			util.sendBirthadyEmail(email, subject, nameByEmail);

		}
	}

	@Override
	public boolean updateDob(TraineeDto dto)  {

		int rowNumber = dto.getId() + 1;
		log.debug("Row Number to update,{}", rowNumber);
		String rowRange = dobSheetName+birthDayStartRow + rowNumber + ":" + birthDayEndRow+rowNumber;
		log.debug("Row Range ,{}", rowRange);
		BirthDayInfoDto birthday = assignToBirthDayDto(dto);
		log.info("Updating BOD details of students,{}", birthday);
		List<List<Object>> values = null;
		try {
			values = Arrays.asList(wrapper.extractDtoDetails(birthday));
		} catch (IllegalAccessException e) {
		log.error("Exception in update DOB service,{}",e);
		}
		if (!values.isEmpty()) {
			List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
			values.set(0, modifiedValues);
			log.debug("values {}", values);
		}
		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		UpdateValuesResponse updated = repository.updateDob(rowRange, valueRange);
		log.info("update response is :{}", updated);
		if (updated != null) {
			return true;
		} else {
			return false;
		}
	}
	

}
