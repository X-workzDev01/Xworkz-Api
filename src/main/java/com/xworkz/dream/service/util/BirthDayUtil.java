package com.xworkz.dream.service.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.BirthDayInfoDto;
import com.xworkz.dream.dto.SheetPropertyDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.BirthadayRepository;
import com.xworkz.dream.service.CacheService;
import com.xworkz.dream.wrapper.DreamWrapper;

@Component
public class BirthDayUtil {

	@Autowired
	private BirthadayRepository repository;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private SheetPropertyDto sheetPropertyDto;
	@Autowired
	private CacheService cacheService;
	private static final Logger log = LoggerFactory.getLogger(BirthDayUtil.class);

	public BirthDayInfoDto assignToBirthDayDto(TraineeDto dto) {
		BirthDayInfoDto birthdayDto = new BirthDayInfoDto();
		birthdayDto.setTraineeEmail(dto.getBasicInfo().getEmail());
		birthdayDto.setBirthDayMailSent(ServiceConstant.NO.toString());
		birthdayDto.setAuditDto(dto.getAdminDto());
		return birthdayDto;
	}

	public List<TraineeDto> getTraineeDetails(DateTimeFormatter dateFormatter, List<List<Object>> listOfDetails,
			List<TraineeDto> listofDtos, List<String> listOfEmail) {
		List<TraineeDto> matchingTrainees = listofDtos
				.stream().filter(
						traineeDto -> traineeDto != null && traineeDto.getBasicInfo() != null
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

	public boolean findAndUpdate(String email, TraineeDto traineeDto) {
		BirthDayInfoDto birthDayInfoDto = updateNewMailStatus(traineeDto);
		log.info("updating birthday mail sent status for the email:{}", email);
		int rowIndex = findRowIndexByEmail(email);
		if (rowIndex != -1) {
			Integer id = rowIndex - 1;
			birthDayInfoDto.setId(id);
			String updateRange = sheetPropertyDto.getDobSheetName() + sheetPropertyDto.getBirthDayStartRow() + rowIndex
					+ ":" + sheetPropertyDto.getBirthDayEndRow() + rowIndex;
			List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(birthDayInfoDto));
			if (!values.isEmpty()) {
				List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
				values.set(0, modifiedValues);
			}
			ValueRange valueRange = new ValueRange();
			valueRange.setValues(values);
			String response = repository.updateDob(updateRange, valueRange);
			if (response != null) {
				cacheService.updateBirthDayInfoInCache("getListOfBirthDayDetails", "listOfBirthDayDetails", email,
						birthDayInfoDto);
				cacheService.getCacheDataByEmail("getListOfBirthDayEmail", "listOfBirthDayEmail", email,
						birthDayInfoDto.getTraineeEmail());
				return true;
			}
		}
		return false;
	}

	public BirthDayInfoDto updateNewMailStatus(TraineeDto dto) {
		BirthDayInfoDto birthdayDto = new BirthDayInfoDto();
		birthdayDto.setTraineeEmail(dto.getBasicInfo().getEmail());
		birthdayDto.setBirthDayMailSent(ServiceConstant.YES.toString());
		birthdayDto.setAuditDto(dto.getAdminDto());
		return birthdayDto;
	}

	public boolean findAndUpdateByEmail(String email, TraineeDto dto, BirthDayInfoDto birthday, TraineeDto traineeDto) {
		log.info("update birthday sheet by email:{}", email);
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
						cacheService.updateBirthDayInfoInCache("getListOfBirthDayDetails", "listOfBirthDayDetails",
								email, birthday);
						cacheService.getCacheDataByEmail("getListOfBirthDayEmail", "listOfBirthDayEmail", email,
								dto.getBasicInfo().getEmail());
						return true;
					}
				}
			}
		}
		return false;
	}

	public int findRowIndexByEmail(String email) {
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

	public boolean assignUpdatedValue(BirthDayInfoDto dto) {
		dto.setBirthDayMailSent(ServiceConstant.NO.toString());
		return yearlyUpdateMailStatus(dto.getTraineeEmail(), dto);
	}

	public boolean yearlyUpdateMailStatus(String email, BirthDayInfoDto birthDayInfoDto) {

		int rowIndex = findRowIndexByEmail(email);
		if (rowIndex != -1) {
			Integer id = rowIndex - 1;
			birthDayInfoDto.setId(id);
			String updateRange = sheetPropertyDto.getDobSheetName() + sheetPropertyDto.getBirthDayStartRow() + rowIndex
					+ ":" + sheetPropertyDto.getBirthDayEndRow() + rowIndex;
			List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(birthDayInfoDto));
			if (!values.isEmpty()) {
				List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
				values.set(0, modifiedValues);
			}
			ValueRange valueRange = new ValueRange();
			valueRange.setValues(values);
			String response = repository.updateDob(updateRange, valueRange);
			cacheService.updateBirthDayInfoInCache("getListOfBirthDayDetails", "listOfBirthDayDetails", email,
					birthDayInfoDto);
			if (response != null) {

				return true;
			}
		}
		return false;
	}

}
