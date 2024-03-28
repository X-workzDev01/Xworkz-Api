package com.xworkz.dream.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.CSR;
import com.xworkz.dream.dto.SheetPropertyDto;
import com.xworkz.dream.dto.SheetsDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.FollowUpRepository;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.service.util.RegistrationUtil;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class RegistrationServiceImpl implements RegistrationService {
	@Autowired
	private RegisterRepository repo;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private DreamUtil util;
	@Autowired
	private BirthadayService service;
	@Autowired
	private FollowUpService followUpService;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private CsrService csrService;
	@Autowired
	private RegistrationUtil registrationUtil;
	@Autowired
	private FollowUpRepository followupRepo;
	@Autowired
	private SheetPropertyDto sheetPropertyDto;

	private static final Logger log = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public ResponseEntity<String> writeData(String spreadsheetId, TraineeDto dto) {
		try {
			log.info("Writing data for TraineeDto: {}", dto);
			assignCsrDto(dto);
			registrationUtil.cgpaToPercentage(dto);
			wrapper.setValuesForTraineeDto(dto);
			List<Object> list = wrapper.extractDtoDetails(dto);
			repo.writeData(spreadsheetId, list);

			if (dto.getBasicInfo().getEmail() != null) {
				log.info("adding email to the cache", dto.getBasicInfo().getEmail());
				cacheService.addEmailToCache("emailData", spreadsheetId, dto.getBasicInfo().getEmail());
			}
			if (dto.getBasicInfo().getContactNumber() != null) {
				log.info("adding contact number to the cache", dto.getBasicInfo().getContactNumber());
				cacheService.addContactNumberToCache("contactData", spreadsheetId,
						dto.getBasicInfo().getContactNumber());
			}
			log.info("adding register data to the cache:{}", list);
			cacheService.updateCache("sheetsData", "listOfTraineeData", list);
			log.info("adding to follow up:", dto);
			boolean status = followUpService.addToFollowUp(dto, spreadsheetId);

			if (status) {
				log.info("Data written successfully to spreadsheetId and Added to Follow Up: {}");
				log.info("saving birthday information", dto);
				service.saveBirthDayInfo(dto);
				boolean sent = util.sendCourseContent(dto.getBasicInfo().getEmail(),
						dto.getBasicInfo().getTraineeName());

				if (sent) {
					return ResponseEntity.ok("Data written successfully, Added to follow Up, sent course content");
				} else {
					return ResponseEntity.ok("Email not sent, Data written successfully, Added to follow Up");
				}

			}

			return ResponseEntity.ok("Data written successfully, not added to Follow Up");
		} catch (Exception e) {
			log.error("Error processing request:{} ", e.getMessage());
			return ResponseEntity.ok("Failed to process the request");
		}
	}

	private void assignCsrDto(TraineeDto dto) {
		String uniqueId = csrService.generateUniqueID();
		CSR csr = new CSR();
		log.info("set {} if offeredAs a CSR", dto.getCourseInfo().getOfferedAs()
				.equalsIgnoreCase(ServiceConstant.CSR_Offered.toString().replace('_', ' ')) ? "1" : "0");
		csr.setCsrFlag(dto.getCourseInfo().getOfferedAs()
				.equalsIgnoreCase(ServiceConstant.CSR_Offered.toString().replace('_', ' ')) ? "1" : "0");
		csr.setActiveFlag(ServiceConstant.ACTIVE.toString());
		csr.setAlternateContactNumber(0l);
		csr.setUniqueId(dto.getCourseInfo().getOfferedAs().equalsIgnoreCase(
				ServiceConstant.CSR_Offered.toString().replace('_', ' ')) ? uniqueId : ServiceConstant.NA.toString());
		csr.setUsnNumber(ServiceConstant.NA.toString());
		dto.setCsrDto(csr);
	}

	@Override
	public ResponseEntity<String> emailCheck(String spreadsheetId, String email) {

		try {
			List<List<Object>> values = repo.getEmails(spreadsheetId, email);
			for (List<Object> row : values) {
				if (row != null && !row.isEmpty() && row.get(0) != null
						&& row.get(0).toString().equalsIgnoreCase(email)) {
					log.info("Email exists in spreadsheetId: {}", spreadsheetId);
					return ResponseEntity.status(HttpStatus.CREATED).body("Email exists");

				}
			}
			log.info("Email does not exist in spreadsheetId: {}", spreadsheetId);
			return ResponseEntity.ok("Email does not exist");

		} catch (Exception e) {
			log.error("An error occurred while checking email in spreadsheetId: {}", spreadsheetId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	@Override
	public ResponseEntity<String> contactNumberCheck(String spreadsheetId, Long contactNumber) {
		try {
			List<List<Object>> values = repo.getContactNumbers(spreadsheetId);
			for (List<Object> row : values) {
				if (row != null && !row.isEmpty() && row.get(0) != null
						&& row.get(0).toString().equals(String.valueOf(contactNumber))) {
					log.info("Contact Number exists in spreadsheetId: {}", spreadsheetId);
					return ResponseEntity.status(HttpStatus.CREATED).body("Contact Number exists");
				}
			}
			log.info("Contact Number does not exist in spreadsheetId: {}", spreadsheetId);
			return ResponseEntity.ok("Contact Number does not exist");
		} catch (Exception e) {
			log.error("An error occurred while checking Contact Number in spreadsheetId: {}", spreadsheetId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	@Override
	public ResponseEntity<SheetsDto> readData(String spreadsheetId, int startingIndex, int maxRows, String courseName,
			String collegeName, String followupStatus, String offeredAs, String yearOfPassOut) {
		SheetsDto traineeData = new SheetsDto();
		List<TraineeDto> traineeDetails = traineeData();
		Comparator<TraineeDto> registrationDateComparator = Comparator
				.comparing(trainee -> trainee.getOthersDto().getRegistrationDate());
		List<TraineeDto> traineeDtos = traineeDetails.stream().sorted(registrationDateComparator.reversed())
				.collect(Collectors.toList());
		Predicate<TraineeDto> predicate = registrationUtil.getBySelection(courseName, collegeName, followupStatus,
				offeredAs, yearOfPassOut);
		List<TraineeDto> listOfDto = new ArrayList<TraineeDto>();
		if (predicate != null) {
			listOfDto = traineeDtos.stream().filter(predicate).collect(Collectors.toList());
			traineeData.setSize(listOfDto.size());
			traineeData
					.setSheetsData(listOfDto.stream().skip(startingIndex).limit(maxRows).collect(Collectors.toList()));
		} else {
			traineeData.setSize(traineeDtos.size());
			traineeData.setSheetsData(
					traineeDtos.stream().skip(startingIndex).limit(maxRows).collect(Collectors.toList()));
		}
		return ResponseEntity.ok(traineeData);

	}

	private List<TraineeDto> traineeData() {
		List<List<Object>> dataList = repo.readData(sheetPropertyDto.getSheetId());
		List<List<Object>> followupList = followupRepo.getFollowUpDetails(sheetPropertyDto.getSheetId());
		List<TraineeDto> listOfTrainee = registrationUtil.readOnlyActiveData(dataList);
		List<TraineeDto> traineeDtos = listOfTrainee.stream()
				.peek(traineeDto -> followupList.stream().map(wrapper::listToFollowUpDTO)
						.filter(followup -> traineeDto.getId().equals(followup.getId())).findFirst()
						.ifPresent(followup -> traineeDto.setFollowupStatus(followup.getCurrentStatus())))
				.collect(Collectors.toList());

		return traineeDtos;

	}

	@Override
	public List<TraineeDto> filterData(String spreadsheetId, String searchValue, String courseName, String collegeName,
			String followupStatus, String offeredAs, String yearOfPassOut) {
		List<TraineeDto> traineeDtos = new ArrayList<TraineeDto>();
		List<TraineeDto> listOfTrainee = traineeData();
		if (listOfTrainee != null && searchValue != null) {
			Predicate<TraineeDto> predicate =registrationUtil.findBySearchValue(searchValue, courseName, collegeName, followupStatus,
					offeredAs, yearOfPassOut);
			if(predicate!=null) {
				traineeDtos=listOfTrainee.stream().filter(predicate).collect(Collectors.toList());
			}
		}

		return traineeDtos;
	}	

	private int findRowIndexByEmail(String spreadsheetId, String email) {
		log.info("Finding row index by email in spreadsheetId: {} for email: {}", spreadsheetId, email);
		List<List<Object>> data = repo.getEmails(spreadsheetId, email);
		List<List<Object>> values = data;
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				List<Object> row = values.get(i);
				if (row.size() > 0 && row.get(0).toString().equalsIgnoreCase(email)) {
					log.info("Found row index {} for email: {}", i + 3, email);
					return i + 3;
				}
			}
		}
		log.info("Email {} not found in the spreadsheet.", email);
		return -1;
	}

	@Override
	public ResponseEntity<String> update(String spreadsheetId, String email, TraineeDto dto) {
		log.info("Updating data in spreadsheetId: {} for email: {}", spreadsheetId, email);
		wrapper.setAdminDto(dto);
		if (email != null && dto.getBasicInfo().getEmail() == "") {
			dto.getBasicInfo().setEmail(email);
		}
		if (email != null && dto.getBasicInfo().getEmail() != null) {
			dto.getBasicInfo().setEmail(dto.getBasicInfo().getEmail());
		}
		wrapper.setFieldValueAsNa(dto);
		registrationUtil.cgpaToPercentage(dto);
		TraineeDto traineeDto = registrationUtil.getDetailsByEmail(email);
		int rowIndex = findRowIndexByEmail(spreadsheetId, email);
		if (rowIndex != -1) {
			log.info("Found row index {} for email: {}", rowIndex, email);
			String range = sheetPropertyDto.getTraineeSheetName() + sheetPropertyDto.getRowStartRange() + rowIndex + ":"
					+ sheetPropertyDto.getRowEndRange() + rowIndex;
			List<List<Object>> values = Arrays.asList(wrapper.extractDtoDetails(dto));
			if (!values.isEmpty()) {
				List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
				values.set(0, modifiedValues);
			}
			ValueRange valueRange = new ValueRange();
			valueRange.setValues(values);

			UpdateValuesResponse updated = repo.update(spreadsheetId, range, valueRange);
			if (updated != null && !updated.isEmpty()) {
				service.updateDob(email, dto);
				followUpService.updateFollowUp(spreadsheetId, email, dto);
				cacheService.getCacheDataByEmail("sheetsData", "listOfTraineeData", email, dto);
				log.info("Updated Successfully. Email: {}", email);
				cacheService.getCacheDataByEmail("emailData", spreadsheetId, email, dto.getBasicInfo().getEmail());
				updateCacheValues(spreadsheetId, email, dto, traineeDto);
				return ResponseEntity.ok("Updated Successfully");
			} else {
				log.error("Error updating data. Email: {}", email);
				return ResponseEntity.ok("error");
			}

		} else {
			log.warn("Email not found: {}", email);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
		}
	}

	private void updateCacheValues(String spreadsheetId, String email, TraineeDto dto, TraineeDto traineeDto) {
		cacheService.getCacheDataByEmail("sheetsData", "listOfTraineeData", email, dto);
		log.info("Updated Successfully. Email: {}", email);
		cacheService.getCacheDataByEmail("emailData", spreadsheetId, email, dto.getBasicInfo().getEmail());
		cacheService.EmailUpdate("usnNumber", "listOfUsnNumbers", traineeDto.getCsrDto().getUsnNumber(),
				dto.getCsrDto().getUsnNumber());
		cacheService.EmailUpdate("uniqueNumber", "listofUniqueNumbers", traineeDto.getCsrDto().getUniqueId(),
				dto.getCsrDto().getUniqueId());
		cacheService.updateValue("alternativeNumber", "listOfAlternativeContactNumbers",
				traineeDto.getCsrDto().getAlternateContactNumber(), dto.getCsrDto().getAlternateContactNumber());
		cacheService.updateValue("contactData", spreadsheetId, traineeDto.getBasicInfo().getContactNumber(),
				dto.getBasicInfo().getContactNumber());
	}

	@Override
	public TraineeDto getDetailsByEmail(String spreadsheetId, String email) {
		List<List<Object>> data = repo.readData(spreadsheetId);
		TraineeDto trainee = data.stream().filter(list -> list.get(2).toString().contentEquals(email)).findFirst()
				.map(wrapper::listToDto).orElse(null);
		if (trainee != null) {
			return trainee;
		} else {
			return null;
		}
	}

	@Override
	public ResponseEntity<List<TraineeDto>> getSearchSuggestion(String spreadsheetId, String value, String courseName,
			String collegeName, String followupStatus, String offeredAs, String yearOfPassOut) {
		List<TraineeDto> suggestion = new ArrayList<>();
		if (value != null && !value.isEmpty()) {
			List<TraineeDto> listOfTrainee = traineeData();
			Predicate<TraineeDto> predicate =registrationUtil.findSuggestion(value, courseName, collegeName, followupStatus,
					offeredAs, yearOfPassOut);
			if(predicate!=null) {
				suggestion=listOfTrainee.stream().filter(predicate).collect(Collectors.toList());
			}
		}
		return ResponseEntity.ok(suggestion);
	}

	@Override
	public String checkworkzEmail(String email) {
		log.debug("checkworkzEmail:{}", email);
		List<List<Object>> listOfTraineeData = repo.readData(sheetPropertyDto.getSheetId());
		if (listOfTraineeData != null && email != null) {
			TraineeDto dto = listOfTraineeData.stream().map(wrapper::listToDto)
					.filter(traineeDto -> traineeDto != null && traineeDto.getOthersDto() != null
							&& traineeDto.getOthersDto().getXworkzEmail() != null
							&& traineeDto.getOthersDto().getXworkzEmail().equalsIgnoreCase(email))
					.findFirst().orElse(null);
			if (dto != null) {
				return "Email Exist";
			} else {
				return "Email Not Exist";
			}
		}
		return "Email Not Exist";
	}

}
