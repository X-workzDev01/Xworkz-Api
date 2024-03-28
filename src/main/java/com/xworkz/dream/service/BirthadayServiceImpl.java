package com.xworkz.dream.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.BirthdayDataDto;
import com.xworkz.dream.dto.BirthdayDetailsDto;
import com.xworkz.dream.dto.SheetPropertyDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.BirthadayRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.service.util.BirthDayUtil;
import com.xworkz.dream.service.util.RegistrationUtil;
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
	@Autowired
	private SheetPropertyDto sheetPropertyDto;
	@Autowired
	private BirthDayUtil birthDayUtil;
	@Autowired
	private RegisterRepository registrationRepo;
	@Autowired
	private RegistrationUtil registrationUtil;
	@Autowired
	private CacheService cacheService;

	private static final Logger log = LoggerFactory.getLogger(BirthadayServiceImpl.class);

	@Override
	public String saveBirthDayInfo(TraineeDto dto) {
		BirthDayInfoDto birthday = birthDayUtil.assignToBirthDayDto(dto);
		List<Object> list = wrapper.extractDtoDetails(birthday);
		boolean save = repository.saveBirthDayDetails(list);
		if (save != false) {
			cacheService.updateCache("getListOfBirthDayDetails", "listOfBirthDayDetails", list);
			cacheService.addToCache("getListOfBirthDayEmail", "listOfBirthDayEmail", birthday.getTraineeEmail());
			log.info("Birth day information added successfully");
			return "Birth day information added successfully";
		}
		log.info("Birth day information not added");
		return "Birth day information Not added";
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
				List<TraineeDto> matchingTrainees = birthDayUtil.getTraineeDetails(dateFormatter, listOfDetails,
						listofDtos, listOfEmail);
				if (matchingTrainees != null) {
					matchingTrainees.stream().forEach(trainee -> {
						String email = trainee.getBasicInfo().getEmail();
						String name = trainee.getBasicInfo().getTraineeName();
						log.info("sending birthday wishes to {} and email is:{}", name, email);
						boolean mailSent = util.sendBirthadyEmail(email, subject, name);
						if (mailSent) {
							log.info("Birthday mail sent successfully:{}", mailSent);
							updateMailStatus(email);
						}
					});
				}
			}
		}
	}
	public boolean updateMailStatus(String email) {
		TraineeDto traineeDto = registrationUtil.getDetailsByEmail(email);
		log.info("updating mail status for the email:{}", email);
		return birthDayUtil.findAndUpdate(email, traineeDto);
	}

	@Override
	public boolean updateDob(String email, TraineeDto dto) {

		if (email != null && dto.getBasicInfo().getEmail() == "") {
			dto.getBasicInfo().setEmail(email);
		}
		if (email != null && dto.getBasicInfo().getEmail() != null) {
			dto.getBasicInfo().setEmail(dto.getBasicInfo().getEmail());
		}
		TraineeDto traineeDto = registrationUtil.getDetailsByEmail(email);
		BirthDayInfoDto birthday = getDetailsByEmail(email);
		if (birthday.getBirthDayMailSent() != null
				&& !birthday.getBirthDayMailSent().equalsIgnoreCase(ServiceConstant.YES.toString())) {
			birthday.setBirthDayMailSent(ServiceConstant.NO.toString());
		}
		birthday.setTraineeEmail(dto.getBasicInfo().getEmail());
		birthday.setAuditDto(dto.getAdminDto());
		boolean updateResponse = birthDayUtil.findAndUpdateByEmail(email, dto, birthday, traineeDto);
		if (updateResponse) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateBirthDayMailStatusYearly() {
		List<List<Object>> listOfDetails = repository.getBirthadayDetails(sheetPropertyDto.getSheetId());
		if (listOfDetails != null) {
			List<BirthDayInfoDto> listOfBirthdayDetails = listOfDetails.stream().map(wrapper::listToBirthDayInfo)
					.filter(dto -> dto != null
							&& dto.getBirthDayMailSent().equalsIgnoreCase(ServiceConstant.YES.toString()))
					.collect(Collectors.toList());
			listOfBirthdayDetails.stream().forEach(dto -> birthDayUtil.assignUpdatedValue(dto));
		}
		return false;
	}

	public BirthDayInfoDto getDetailsByEmail(String email) {
		if (email != null) {
			log.info("find BirthDayInfoDto by email:{}", email);
			List<List<Object>> listOfDetails = repository.getBirthadayDetails(sheetPropertyDto.getSheetId());
			if (listOfDetails != null) {
				BirthDayInfoDto birthDayInfoDto = listOfDetails.stream().map(wrapper::listToBirthDayInfo)
						.filter(birthdayInfoDto -> birthdayInfoDto != null
								&& birthdayInfoDto.getTraineeEmail().equalsIgnoreCase(email))
						.findFirst().orElse(null);
				if (birthDayInfoDto != null) {
					return birthDayInfoDto;
				} else {
					return birthDayInfoDto;
				}
			}
		}
		return null;
	}

	@Override
	public BirthdayDataDto getBirthdays(String spreadsheetId, int startingIndex, int maxRows, String date,
			String courseName, String month) {
		
		Comparator<TraineeDto> comparator = Comparator
				.comparing(trainee -> trainee.getBasicInfo().getTraineeName());
		List<BirthDayInfoDto> mailSentList = repository.getBirthadayDetails(spreadsheetId).stream()
				.map(wrapper::listToBirthDayInfo).sorted().collect(Collectors.toList());
		List<TraineeDto> listOfTrainees = registrationRepo.readData(spreadsheetId).stream().map(wrapper::listToDto).sorted(comparator)
				.collect(Collectors.toList());
		List<BirthdayDetailsDto> listofBirthday = new ArrayList<>();

		listOfTrainees.stream().forEach(dto -> {
			if (dto.getBasicInfo() != null && dto.getCourseInfo().getCourse() != null) {
				listofBirthday.add(
						new BirthdayDetailsDto(dto.getId(), dto.getBasicInfo(), dto.getCourseInfo().getCourse(), null));
			}
		});

		listofBirthday.stream().forEach(birthdayDetailsDto -> {
			mailSentList.stream().forEach(birthDayInfoDto -> {
				if (birthDayInfoDto.getTraineeEmail().equalsIgnoreCase(birthdayDetailsDto.getBasicInfoDto().getEmail())) {
					if (!birthDayInfoDto.getBirthDayMailSent().equals(null) && !birthDayInfoDto.getBirthDayMailSent().equals("")) {
						birthDayInfoDto.setBirthDayMailSent(birthDayInfoDto.getBirthDayMailSent());
					} else {
						birthDayInfoDto.setBirthDayMailSent("NA");
					}
				}
			});
		});

		Predicate<BirthdayDetailsDto> predicate = birthDayUtil.predicateBySelected(date, courseName, month);
		if (predicate != null) {
			List<BirthdayDetailsDto> filteredList = listofBirthday.stream().filter(predicate)
					.collect(Collectors.toList());
			List<BirthdayDetailsDto> limitedRows = filteredList.stream().skip(startingIndex).limit(maxRows)
					.collect(Collectors.toList());
			return new BirthdayDataDto(limitedRows, filteredList.size());
		}
		List<BirthdayDetailsDto> limitedRows = listofBirthday.stream().skip(startingIndex).limit(maxRows)
				.collect(Collectors.toList());

		return new BirthdayDataDto(limitedRows, listofBirthday.size());
	}

}
