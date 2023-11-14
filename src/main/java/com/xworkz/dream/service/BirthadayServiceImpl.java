package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

	private static final Logger log = LoggerFactory.getLogger(BirthadayServiceImpl.class);

	@Override
	public ResponseEntity<String> saveBirthDayInfo(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws IllegalAccessException, IOException {
		BirthDayInfoDto birthday = new BirthDayInfoDto();
		List<List<Object>> data = repository.getBirthDayId(spreadsheetId).getValues();
		int size = data != null ? data.size() : 0;
		birthday.setDto(dto.getBasicInfo());
		birthday.setId(size += 1);
		List<Object> list = wrapper.extractDtoDetails(birthday);

		boolean save = repository.saveBirthDayDetails(spreadsheetId, list);
		if (save != false) {
			log.info("Birth day information added successfully");
			return ResponseEntity.ok("Birth day information added successfully");
		}
		log.info("Birth day information not added");
		return ResponseEntity.ok("Birth day information Not added");
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
		log.info("Birthday details: {}", birthdayDetails);
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<String> emailsToSend = birthdayDetails.stream().filter(row -> {
			LocalDate dob = LocalDate.parse((String) row.get(4), dateFormatter);
			return dob.getMonth() == currentDate.getMonth() && dob.getDayOfMonth() == currentDate.getDayOfMonth();
		}).map(row -> (String) row.get(2)).collect(Collectors.toList());
		for (String email : emailsToSend) {
			String nameByEmail = findNameByEmail(email);
			util.sendBirthadyEmail(email, subject, nameByEmail);
			log.info("Sent birthday email to '{}' with name '{}'", email, nameByEmail);
		}
	}

}
