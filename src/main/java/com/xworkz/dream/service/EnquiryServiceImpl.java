package com.xworkz.dream.service;

import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

	private static final Logger log = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public ResponseEntity<String> writeDataEnquiry(String spreadsheetId, TraineeDto dto, HttpServletRequest request)
			throws MessagingException, TemplateException {
		try {
			log.info("Writing data for TraineeDto: {}", dto);
			wrapper.setValuesForTraineeDto(dto);

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
			log.error("Error processing request: " + e.getMessage(), e);
			return ResponseEntity.ok("Failed to process the request");
		}

	}

	@Override
	public boolean addEnquiry(EnquiryDto enquiryDto, String spreadsheetId, HttpServletRequest request) {
		TraineeDto traineeDto = new TraineeDto();
		EnquiryDto validatedEnquiryDto = wrapper.validateEnquiry(enquiryDto);

		traineeDto.setCourseInfo(new CourseDto("NA"));
		traineeDto.setOthersDto(new OthersDto("NA"));
		traineeDto.setAdminDto(enquiryDto.getAdminDto());
		traineeDto.setBasicInfo(enquiryDto.getBasicInfo());
		traineeDto.setEducationInfo(enquiryDto.getEducationInfo());

		try {
			writeDataEnquiry(spreadsheetId, traineeDto, request);
		} catch (MessagingException | TemplateException e) {
			log.error("Error Writing enquiry data to sheet: " + e.getMessage(), e);
		}
		return true;

	}

}
