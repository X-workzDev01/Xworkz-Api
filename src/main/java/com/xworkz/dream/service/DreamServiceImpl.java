package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.AttendanceDto;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.interfaces.EmailableClient;
import com.xworkz.dream.repository.AttendanceRepository;
import com.xworkz.dream.repository.FollowUpRepository;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class DreamServiceImpl implements DreamService {

	@Autowired
	private EmailableClient emailableClient;
	@Value("${sheets.liveKey}")
	private String API_KEY;
	@Value("${login.sheetId}")
	private String sheetId;
	@Value("${sheets.attendanceInfoRange}")
	private String attendanceInfoRange;
	@Autowired
	private FollowUpRepository repository;
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private AttendanceRepository attendanceRepository;
	@Autowired
	private AttendanceService attendanceService;
	@Autowired
	private CacheService cacheService;

	private static final Logger logger = LoggerFactory.getLogger(DreamServiceImpl.class);

	@Override
	public String verifyEmails(String email) {
		logger.info("Verifying email: {}", email);
		return emailableClient.verifyEmail(email, API_KEY);
	}

	@Override
	public Boolean addJoined(String status, String courseName) throws IOException, IllegalAccessException {
		List<List<Object>> followUpDetails = repository.getFollowUpDetails(sheetId);

		if (followUpDetails != null && !followUpDetails.isEmpty()) {
			List<FollowUpDto> optionalFollowupDto = filterFollowUpDetails(status, courseName, followUpDetails);

			for (FollowUpDto followUpDto : optionalFollowupDto) {
				AttendanceDto saveAttendance = wrapper.saveAttendance(followUpDto);

				if (!attendanceService.traineeAlreadyAdded(courseName, saveAttendance.getId())) {
					processAttendance(saveAttendance, courseName);
				} else {
					return false;
				}
			}
			return true; 
		} else {
			return false; 
		}
	}

	private List<FollowUpDto> filterFollowUpDetails(String status, String courseName,
			List<List<Object>> followUpDetails) {
		return followUpDetails.stream().filter(row -> courseName.equals(row.get(6)))
				.filter(row -> status.equals(row.get(8))).map(this::createFollowupDtoFromDetails)
				.collect(Collectors.toList());
	}

	private void processAttendance(AttendanceDto saveAttendance, String courseName)
			throws IOException, IllegalAccessException {
		List<Object> extractDtoDetails = wrapper.extractDtoDetails(saveAttendance);
		extractDtoDetails.remove(4);

		attendanceRepository.writeAttendance(sheetId, extractDtoDetails, attendanceInfoRange);

		cacheService.addAttendancdeToCache("attendanceData", "listOfAttendance", extractDtoDetails);

	}

	private FollowUpDto createFollowupDtoFromDetails(List<Object> details) {
		FollowUpDto listToFollowUpDTO = wrapper.listToFollowUpDTO(details);
		return listToFollowUpDTO;
	}

}
