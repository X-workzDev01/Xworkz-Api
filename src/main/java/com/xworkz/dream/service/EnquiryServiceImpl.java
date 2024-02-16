package com.xworkz.dream.service;

import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.CSR;
import com.xworkz.dream.dto.CourseDto;
import com.xworkz.dream.dto.EnquiryDto;
import com.xworkz.dream.dto.OthersDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.util.DreamUtil;
import com.xworkz.dream.wrapper.DreamWrapper;

import freemarker.template.TemplateException;

@Service
public class EnquiryServiceImpl implements EnquiryService {

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
	private static final Logger log = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public ResponseEntity<String> writeDataEnquiry(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws MessagingException, TemplateException {
		try {
			log.info("Writing data for TraineeDto: {}", dto);
			String uniqueId = csrService.generateUniqueID();
			CSR csr = new CSR();
			log.info("set {} if offeredAs a CSR",
					dto.getCourseInfo().getOfferedAs().equalsIgnoreCase(ServiceConstant.CSR.toString()) ? "1" : "0");
			csr.setCsrFlag(
					dto.getCourseInfo().getOfferedAs().equalsIgnoreCase(ServiceConstant.CSR.toString()) ? "1" : "0");
			csr.setActiveFlag(ServiceConstant.ACTIVE.toString());
			csr.setAlternateContactNumber(0l);
			csr.setUniqueId(
					dto.getCourseInfo().getOfferedAs().equalsIgnoreCase(ServiceConstant.CSR.toString()) ? uniqueId
							: ServiceConstant.NA.toString());
			csr.setUsnNumber(ServiceConstant.NA.toString());
			dto.setCsrDto(csr);
			wrapper.setValuesForTraineeDto(dto);

			List<Object> list = wrapper.extractDtoDetails(dto);

			repo.writeData(spreadsheetId, list);
			cacheService.updateCache("sheetsData", "listOfTraineeData", list);
			if (dto.getBasicInfo().getEmail() != null) {
				cacheService.addEmailToCache("emailData", spreadsheetId, dto.getBasicInfo().getEmail());

			}
			if (dto.getBasicInfo().getContactNumber() != null) {
				cacheService.addContactNumberToCache("contactData", spreadsheetId,
						dto.getBasicInfo().getContactNumber());

			}
			log.info("Saving birth details: {}", dto);
			service.saveBirthDayInfo(spreadsheetId, dto, request);

			boolean status = followUpService.addToFollowUpEnquiry(dto, spreadsheetId);

			if (status) {
				log.info("Data written successfully to spreadsheetId and Added to Follow Up: {}", spreadsheetId);
				util.sms(dto);
				boolean sent = false;
				if (!dto.getBasicInfo().getEmail().contains("@dummy.com")) {

					sent = util.sendCourseContent(dto.getBasicInfo().getEmail(), dto.getBasicInfo().getTraineeName());
				}

				if (sent) {
					return ResponseEntity.ok("Data written successfully, Added to follow Up, sent course content");
				}
			} else {
				return ResponseEntity.ok("Email not sent, Data written successfully, Added to follow Up");
			}
			return ResponseEntity.ok("Data written successfully, not added to Follow Up");
		} catch (Exception e) {
			log.error("Error processing request:{} ", e.getMessage());
			return ResponseEntity.ok("Failed to process the request");
		}

	}

	@Override
	public boolean addEnquiry(EnquiryDto enquiryDto, String spreadsheetId, HttpServletRequest request) {
		wrapper.validateEnquiry(enquiryDto);
		TraineeDto traineeDto = new TraineeDto();
		traineeDto.setCourseInfo(new CourseDto(ServiceConstant.NA.toString()));
		traineeDto.setOthersDto(new OthersDto(ServiceConstant.NA.toString()));
		traineeDto.setBasicInfo(enquiryDto.getBasicInfo());
		traineeDto.setEducationInfo(enquiryDto.getEducationInfo());
		traineeDto.setAdminDto(enquiryDto.getAdminDto());

		try {
			writeDataEnquiry(spreadsheetId, traineeDto, request);
		} catch (MessagingException | TemplateException e) {
			log.error("Error Writing enquiry data to sheet:{} ", e.getMessage());
		}
		return true;

	}

}
