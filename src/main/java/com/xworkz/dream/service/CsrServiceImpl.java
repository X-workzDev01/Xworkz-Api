package com.xworkz.dream.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.CSR;
import com.xworkz.dream.dto.CourseDto;
import com.xworkz.dream.dto.CsrDto;
import com.xworkz.dream.dto.OthersDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class CsrServiceImpl implements CsrService {

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
	@Value("${login.sheetId}")
	private String spreadsheetId;
	private static final int MAX_ATTEMPTS = 99999;
	private static Set<Integer> generatedIDs = new HashSet<>();

	private static final Logger log = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public ResponseEntity<String> validateAndRegister(TraineeDto dto, HttpServletRequest request) {
		try {
			log.info("Writing data for TraineeDto: {}", dto);
			wrapper.setValuesForCSRDto(dto);
			List<Object> list = wrapper.extractDtoDetails(dto);
			repo.writeData(spreadsheetId, list);
			addToCache(dto, request, list);
			boolean status = followUpService.addCsrToFollowUp(dto, spreadsheetId);

			if (status) {
				log.info("Data written successfully to spreadsheetId and Added to Follow Up: {}", spreadsheetId);
				util.csrSmsSent(dto.getBasicInfo().getTraineeName(), dto.getBasicInfo().getContactNumber().toString());
				boolean sent = util.csrEmailSent(dto);

				if (sent) {
					log.info(dto.getBasicInfo().getEmail());
					return ResponseEntity.ok("Data written successfully, Added to follow Up, sent course content");

				}
			} else {
				return ResponseEntity.ok("Email not sent, Data written successfully, Added to follow Up");
			}
			return ResponseEntity.ok("Data written successfully, not added to Follow Up");
		} catch (Exception e) {
			log.error("Error processing request: {}",e);
			return ResponseEntity.ok("Failed to process the request");
		}
 
	}

	public void addToCache(TraineeDto dto, HttpServletRequest request, List<Object> list)
			throws IllegalAccessException, IOException {
		cacheService.updateCache("sheetsData", "listOfTraineeData", list);
		if (dto.getBasicInfo().getEmail() != null) {
			cacheService.addEmailToCache("emailData", spreadsheetId, dto.getBasicInfo().getEmail());
		}
		if (dto.getBasicInfo().getContactNumber() != null) {
			cacheService.addContactNumberToCache("contactData", spreadsheetId, dto.getBasicInfo().getContactNumber());
		}
		log.info("Saving birth details: {}", dto);
		service.saveBirthDayInfo(spreadsheetId, dto, request);
		// adding alternative number to cache
		cacheService.addContactNumberToCache("alternativeNumber", "listOfAlternativeContactNumbers",
				dto.getCsrDto().getAlternateContactNumber());
		// adding USN number to cache
		cacheService.addEmailToCache("usnNumber", "listOfUsnNumbers", dto.getCsrDto().getUsnNumber());
		// adding Unique Number to caches
		cacheService.addEmailToCache("uniqueNumber", "listofUniqueNumbers", dto.getCsrDto().getUniqueId());
	}

	public boolean registerCsr(CsrDto csrDto, HttpServletRequest request) throws IOException {
		TraineeDto traineeDto = new TraineeDto();
		CSR csr = new CSR();
		traineeDto.setCourseInfo(new CourseDto(ServiceConstant.NA.toString()));
		traineeDto.setOthersDto(new OthersDto(ServiceConstant.NA.toString()));
		traineeDto.setBasicInfo(csrDto.getBasicInfo());
		traineeDto.setEducationInfo(csrDto.getEducationInfo());
		traineeDto.getCourseInfo().setOfferedAs(csrDto.getOfferedAs());
		String uniqueId = generateUniqueID();
		log.info("set {} if offeredAs a CSR",
				traineeDto.getCourseInfo().getOfferedAs().equalsIgnoreCase(ServiceConstant.CSR.toString()) ? "1" : "0");
		csr.setCsrFlag(traineeDto.getCourseInfo().getOfferedAs().equalsIgnoreCase(ServiceConstant.CSR.toString()) ? "1" : "0");
		csr.setActiveFlag(ServiceConstant.ACTIVE.toString());
		csr.setAlternateContactNumber(csrDto.getAlternateContactNumber());
		csr.setUniqueId(traineeDto.getCourseInfo().getOfferedAs().equalsIgnoreCase(ServiceConstant.CSR.toString()) ? uniqueId : ServiceConstant.NA.toString());
		csr.setUsnNumber(csrDto.getUsnNumber());
		traineeDto.setCsrDto(csr);
		validateAndRegister(traineeDto, request);

		return true;
	}

	@Override
	public boolean checkContactNumber(Long contactNumber) throws IOException {
		boolean isExists = false;
		if (contactNumber != null) {
			List<List<Object>> listOfC_number = repo.getContactNumbers(spreadsheetId);
			List<List<Object>> listOfA_number = repo.getAlternativeNumber(spreadsheetId);
			log.info("checking contact number is sheet {}", contactNumber);
			isExists = containsContactNumber(listOfC_number, contactNumber)
					|| containsContactNumber(listOfA_number, contactNumber);
		}
		return isExists;
	}

	private boolean containsContactNumber(List<List<Object>> listOfNumbers, Long contactNumber) {
		log.info("checking contact number existence in sheet,{}", contactNumber);
		return listOfNumbers != null
				&& listOfNumbers.stream().filter(list -> list != null && !list.isEmpty() && list.get(0) != null)
						.anyMatch(list -> list.get(0).toString().equals(String.valueOf(contactNumber)));
	}

	@Override
	public boolean checkUsnNumber(String usnNumber) throws IOException {
		log.info("check Usn Number ");
		if (usnNumber != null) {
			List<List<Object>> listOfUsn = repo.getUsnNumber(spreadsheetId);
			return listOfUsn != null
					&& listOfUsn.stream().filter(list -> list != null && !list.isEmpty() && list.get(0) != null)
							.anyMatch(list -> list.get(0).toString().equalsIgnoreCase(usnNumber));
		}
		return false;
	}

	@Override
	public String generateUniqueID() {
		int minValue = 1;
		int maxValue = 99999;
		Random random = new Random();
		int attempts = 0;
		String year = LocalDate.now().toString().substring(2, 4);
		while (attempts < MAX_ATTEMPTS) {
			Integer uniqueID = minValue + random.nextInt(maxValue - minValue + 1);
			if (generatedIDs.add(uniqueID)) {
				if (uniqueID.toString().length() == 4) {
					return ServiceConstant.XBR.toString() + year + "0" + uniqueID.toString();
				} else {
					return ServiceConstant.XBR.toString() + year + uniqueID.toString();
				}
			}
			attempts++;
		}
		log.error("Max attempts has been completed, it may Generate duplicate number");
		throw new IllegalStateException("Unable to generate a unique ID after " + MAX_ATTEMPTS + " attempts.");
	}

	@Override
	public boolean checkUniqueNumber(String uniqueNumber) throws IOException {
		if (uniqueNumber != null) {
			log.info("checking unique number");
			List<List<Object>> listOfUniqueNumber = repo.getUniqueNumbers(spreadsheetId);
			return listOfUniqueNumber != null && listOfUniqueNumber.stream()
					.filter(list -> list != null && !list.isEmpty() && list.get(0) != null)
					.anyMatch(list -> list.get(0).toString().equals(uniqueNumber));
		}
		return false;
	}

}
