package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

	private static final Logger log = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public ResponseEntity<String> validateAndRegister(TraineeDto dto, HttpServletRequest request) {
		try {
			log.info("Writing data for TraineeDto: {}", dto);
			wrapper.setValuesForCSRDto(dto);
			
			List<Object> list = wrapper.extractDtoDetails(dto);
			repo.writeData(spreadsheetId, list);
			cacheService.updateCache("sheetsData", spreadsheetId, list);

			if (dto.getBasicInfo().getEmail() != null) {
				cacheService.addEmailToCache("emailData", spreadsheetId, dto.getBasicInfo().getEmail());

			}
			if (dto.getBasicInfo().getContactNumber() != null) {
				cacheService.addContactNumberToCache("contactData", spreadsheetId,
						dto.getBasicInfo().getContactNumber());

			}
			log.info("Saving birth details: {}", dto);
			service.saveBirthDayInfo(spreadsheetId, dto, request);

			boolean status = followUpService.addCsrToFollowUp(dto, spreadsheetId);

			if (status) {
				log.info("Data written successfully to spreadsheetId and Added to Follow Up: {}", spreadsheetId);
				util.sms(dto);

				boolean sent = util.sendCourseContent(dto.getBasicInfo().getEmail(),
						dto.getBasicInfo().getTraineeName());

				if (sent) {
					return ResponseEntity.ok("Data written successfully, Added to follow Up, sent course content");
				}
			} else {
				return ResponseEntity.ok("Email not sent, Data written successfully, Added to follow Up");
			}
			return ResponseEntity.ok("Data written successfully, not added to Follow Up");
		} catch (Exception e) {
			log.error("Error processing request: " + e.getMessage(), e);
			return ResponseEntity.ok("Failed to process the request");
		}

	}

	@Override
	public boolean registerCsr(CsrDto csrDto, HttpServletRequest request) {
		TraineeDto traineeDto = new TraineeDto();
		traineeDto.setCourseInfo(new CourseDto("NA"));
		traineeDto.setOthersDto(new OthersDto("NA"));
		traineeDto.setBasicInfo(csrDto.getBasicInfo());
		traineeDto.setEducationInfo(csrDto.getEducationInfo());
		traineeDto.getCourseInfo().setOfferedAs(csrDto.getOfferedAs());
		CSR csr = new CSR(csrDto.getUsnNumber(), csrDto.getAlternateContactNumber());
		traineeDto.setCsrDto(csr);
		validateAndRegister(traineeDto, request);
		//adding alternative number to the cache
		cacheService.addContactNumberToCache("alternativeNumber",spreadsheetId,csrDto.getAlternateContactNumber());
		return true;

	}

	@Override
	public boolean checkContactNumber(Long contactNumber) throws IOException {
		boolean isExists = false;
		if (contactNumber != null) {
			List<List<Object>> listOfC_number = repo.getContactNumbers(spreadsheetId);
			List<List<Object>> listOfA_number = repo.getAlternativeNumber(spreadsheetId);
			log.info("null check for list");

			if (listOfC_number != null || listOfA_number != null) {
				log.info("find the contact number");
				for (List<Object> list : listOfC_number) {

					if (list != null && !list.isEmpty() && list.get(0) != null
							&& list.get(0).toString().equals(String.valueOf(contactNumber))) {
						log.info("Contact number found:{}",contactNumber);
						isExists = true;
					}
				}
				if (!isExists) {
					for (List<Object> list : listOfA_number) {

						if (list != null && !list.isEmpty() && list.get(0) != null
								&& list.get(0).toString().equals(String.valueOf(contactNumber))) {
							log.info("Contact number found:{}",contactNumber);
							isExists = true;
						}
					}

				}
			}
		}
		return isExists;

	}

}
