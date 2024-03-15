package com.xworkz.dream.service;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.BirthdayDataDto;
import com.xworkz.dream.dto.BirthdayDetailsDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.BirthadayRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.dto.SheetPropertyDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.BirthadayRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.service.util.RegistrationUtil;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class BirthadayServiceImpl implements BirthadayService {

	@Autowired
	private BirthadayRepository repository;
	@Autowired
	private RegisterRepository registerRepository;
	@Autowired
	private DreamUtil util;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private SheetPropertyDto sheetPropertyDto;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private RegisterRepository registrationRepo;
	@Autowired
	private RegistrationUtil registrationUtil;
	private static final Logger log = LoggerFactory.getLogger(BirthadayServiceImpl.class);

	@Override
	public String saveBirthDayInfo(TraineeDto dto) {
		BirthDayInfoDto birthday = assignToBirthDayDto(dto);
		List<Object> list = wrapper.extractDtoDetails(birthday);
		boolean save = repository.saveBirthDayDetails(list);
		if (save != false) {
			log.info("Birth day information added successfully");
			return "Birth day information added successfully";
		}
		log.info("Birth day information not added");
		return "Birth day information Not added";
	}

	private BirthDayInfoDto assignToBirthDayDto(TraineeDto dto) {
		BirthDayInfoDto birthdayDto = new BirthDayInfoDto();
		birthdayDto.setTraineeEmail(dto.getBasicInfo().getEmail());
		birthdayDto.setBirthDayMailSent(ServiceConstant.NO.toString());
		birthdayDto.setAuditDto(dto.getAdminDto());
		return birthdayDto;
	}

	@Override
	public void sendBirthdayEmails() {
		String subject = "Birthday Wishes : X-workZ";
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<List<Object>> listOfDetails = repository.getBirthadayDetails(sheetPropertyDto.getSheetId());
		List<List<Object>> listOfTraineeDetails = registrationRepo.readData(sheetPropertyDto.getSheetId());
		if (listOfDetails != null && listOfTraineeDetails != null) {
			List<TraineeDto> listofDtos = registrationUtil.readOnlyActiveData(listOfTraineeDetails);
			List<String> listOfEmail = listOfDetails.stream().map(wrapper::listToBirthDayInfo).filter(
					dto -> dto != null && !dto.getBirthDayMailSent().equalsIgnoreCase(ServiceConstant.YES.toString()))
					.map(BirthDayInfoDto::getTraineeEmail).collect(Collectors.toList());
			if (listOfEmail != null && listofDtos != null) {
				List<TraineeDto> matchingTrainees = getTraineeDetails(dateFormatter, listOfDetails, listofDtos,
						listOfEmail);
				if (matchingTrainees != null) {
					matchingTrainees.stream().forEach(trainee -> {
						String email = trainee.getBasicInfo().getEmail();
						String name = trainee.getBasicInfo().getTraineeName();
						System.out.println(name + ":" + email);
						boolean mailSent = util.sendBirthadyEmail(email, subject, name);
						if (mailSent) {
							log.info("Birthday mail sent successfully:{}",mailSent);
							updateMailStatus(email);
						}
					});
				}
			}
		}
	}

	private List<TraineeDto> getTraineeDetails(DateTimeFormatter dateFormatter, List<List<Object>> listOfDetails,
			List<TraineeDto> listofDtos, List<String> listOfEmail) {
		List<TraineeDto> matchingTrainees = listofDtos.stream()
				.filter(traineeDto -> traineeDto != null && traineeDto.getBasicInfo() != null
						&& traineeDto.getBasicInfo().getEmail() != null
						&& listOfEmail.contains(traineeDto.getBasicInfo().getEmail())
						&& listOfDetails.stream().map(wrapper::listToBirthDayInfo)
								.anyMatch(birthDayInfoDto -> birthDayInfoDto.getTraineeEmail()
										.equalsIgnoreCase(traineeDto.getBasicInfo().getEmail())
										&& !traineeDto.getBasicInfo().getDateOfBirth()
												.equals(ServiceConstant.NA.toString())
										&& !traineeDto.getBasicInfo().getEmail().contains("@dummy.com")
										&& LocalDate.parse(traineeDto.getBasicInfo().getDateOfBirth(),
												dateFormatter).isEqual(LocalDate.now())))
				.collect(Collectors.toList());
		return matchingTrainees;
	}

	public boolean updateMailStatus(String email) {
		TraineeDto traineeDto = registrationUtil.getDetailsByEmail(email);
		return findAndUpdate(email, traineeDto);
	}

	private boolean findAndUpdate(String email, TraineeDto traineeDto) {
		int rowIndex = findRowIndexByEmail(email);
		if (rowIndex != -1) {
			String updateRange = sheetPropertyDto.getDobSheetName() + sheetPropertyDto.getBirthDayStartRow() + rowIndex
					+ ":" + sheetPropertyDto.getBirthDayEndRow() + rowIndex;

			BirthDayInfoDto birthdayDto = assignUpdatedValue(traineeDto);
			List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(birthdayDto));
			if (!values.isEmpty()) {
				List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
				values.set(0, modifiedValues);
			}
			ValueRange valueRange = new ValueRange();
			valueRange.setValues(values);
			String response = repository.updateDob(updateRange, valueRange);
			if (response != null) {
				return true;
			}
		}
		return false;
	}

	private BirthDayInfoDto assignUpdatedValue(TraineeDto traineeDto) {
		BirthDayInfoDto birthdayDto = new BirthDayInfoDto();
		birthdayDto.setTraineeEmail(traineeDto.getBasicInfo().getEmail());
		birthdayDto.setBirthDayMailSent(ServiceConstant.YES.toString());
		birthdayDto.setAuditDto(traineeDto.getAdminDto());
		return birthdayDto;
	}

	@Override
	public boolean updateDob(String email, TraineeDto dto) {
		BirthDayInfoDto birthday = assignToBirthDayDto(dto);

		if (email != null && dto.getBasicInfo().getEmail() == "") {
			dto.getBasicInfo().setEmail(email);
		}
		if (email != null && dto.getBasicInfo().getEmail() != null) {
			dto.getBasicInfo().setEmail(dto.getBasicInfo().getEmail());
		}
		TraineeDto traineeDto = registrationUtil.getDetailsByEmail(email);
		boolean updateResponse = findAndUpdateByEmail(email, dto, birthday, traineeDto);
		if (updateResponse) {
			return true;
		}
		return false;
	}

	private boolean findAndUpdateByEmail(String email, TraineeDto dto, BirthDayInfoDto birthday,
			TraineeDto traineeDto) {
		if (traineeDto != null) {
			if (!traineeDto.getBasicInfo().getEmail().equalsIgnoreCase(dto.getBasicInfo().getEmail()) || !traineeDto
					.getBasicInfo().getDateOfBirth().equalsIgnoreCase(dto.getBasicInfo().getDateOfBirth())) {

				int rowIndex = findRowIndexByEmail(email);
				if (rowIndex != -1) {
					String updateRange = sheetPropertyDto.getDobSheetName() + sheetPropertyDto.getBirthDayStartRow()
							+ rowIndex + ":" + sheetPropertyDto.getBirthDayEndRow() + rowIndex;
					List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(birthday));
					if (!values.isEmpty()) {
						List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
						values.set(0, modifiedValues);
					}
					ValueRange valueRange = new ValueRange();
					valueRange.setValues(values);
					String response = repository.updateDob(updateRange, valueRange);
					if (response != null) {
						cacheService.getCacheDataByEmail("getListOfBirthDayEmail", "listOfBirthDayEmail", email,
								dto.getBasicInfo().getEmail());
						return true;
					}
				}
			}
		}
		return false;
	}

	private int findRowIndexByEmail(String email) {
		log.info("Finding row index by email in sheet for email: {}", email);
		List<List<Object>> listOfEmail = repository.getBirthadayEmailList();
		List<List<Object>> values = listOfEmail;
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				List<Object> row = values.get(i);
				if (row.size() > 0 && row.get(0).toString().equalsIgnoreCase(email)) {
					log.info("Found row index {} for email: {}", i + 1, email);
					return i + 2;
				}
			}
		}
		log.info("Email {} not found in the spreadsheet.", email);
		return -1;
	}

	@Override
	public BirthdayDataDto getBirthdays(String spreadsheetId, int startingIndex, int maxRows, String date,
			String courseName) {

		List<TraineeDto> listOfTrainees = registerRepository.readData(spreadsheetId).stream().map(wrapper::listToDto)
				.collect(Collectors.toList());
		BirthdayDataDto dataDto = new BirthdayDataDto();
		List<BirthdayDetailsDto> listofBirthdays = new ArrayList<>();

		for (TraineeDto traineeDto : listOfTrainees) {
			BirthdayDetailsDto dto = new BirthdayDetailsDto();
			dto.setBasicInfoDto(traineeDto.getBasicInfo());
			dto.setCourseName(traineeDto.getCourseInfo().getCourse());
			listofBirthdays.add(dto);
		}

		Predicate<BirthdayDetailsDto> predicate = null;
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");

		if (!courseName.equals("null") && date.equals("null")) {
			predicate = dto -> dto.getCourseName().equalsIgnoreCase(courseName);
		}

		if (courseName.equals("null") && !date.equals("null")) {
			predicate = dto -> LocalDate.parse(dto.getBasicInfoDto().getDateOfBirth()).format(dateFormatter).toString()
					.equals(LocalDate.parse(date).format(dateFormatter).toString());

		}
		if (predicate != null) {
			listofBirthdays = listofBirthdays.stream().filter(predicate).collect(Collectors.toList());
		}

		List<BirthdayDetailsDto> limitedRows = listofBirthdays.stream().skip(startingIndex).limit(maxRows)
				.collect(Collectors.toList());

		dataDto.setListofBirthdays(limitedRows);
		dataDto.setSize(listofBirthdays.size());
		return dataDto;
	}

}
