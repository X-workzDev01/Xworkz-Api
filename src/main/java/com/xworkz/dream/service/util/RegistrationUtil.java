package com.xworkz.dream.service.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.PercentageDto;
import com.xworkz.dream.dto.SheetPropertyDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.wrapper.DreamWrapper;

@Component
public class RegistrationUtil {

	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private RegisterRepository registerRepository;
	@Autowired
	private SheetPropertyDto sheetPropertyDto;

	private static final Logger log = LoggerFactory.getLogger(RegistrationUtil.class);

	public List<TraineeDto> readOnlyActiveData(List<List<Object>> listOfData) {
		List<TraineeDto> traineeDtos = listOfData.stream().map(wrapper::listToDto)
				.filter(traineeDto -> traineeDto != null && traineeDto.getCsrDto().getActiveFlag() != null
						&& traineeDto.getCsrDto().getActiveFlag().equalsIgnoreCase(ServiceConstant.ACTIVE.toString()))
				.collect(Collectors.toList());
		log.info("filtering the active records size is:{}", traineeDtos.size());
		return traineeDtos;
	}

	public List<TraineeDto> convertToTraineeDto(List<List<Object>> dataList) {
		List<TraineeDto> traineeDtos = dataList.stream().map(wrapper::listToDto).collect(Collectors.toList());
		log.info("filtering the active records size is:{}", traineeDtos.size());
		return traineeDtos;
	}

	public TraineeDto getDetailsByEmail(String email) {
		List<List<Object>> listOfTraineeDetails = registerRepository.readData(sheetPropertyDto.getSheetId());
		if (listOfTraineeDetails != null) {
			List<TraineeDto> traineeDtos = readOnlyActiveData(listOfTraineeDetails);
			if (traineeDtos != null) {
				TraineeDto dto = traineeDtos.stream()
						.filter(traineeDto -> traineeDto != null && traineeDto.getBasicInfo().getEmail() != null
								&& traineeDto.getBasicInfo().getEmail().equalsIgnoreCase(email))
						.findFirst().orElse(null);
				if (dto != null) {
					log.info("find trainee details by email:{}", email);
					return dto;
				} else {
					log.error("trainee details not found for the email:{}", email);
					return dto;
				}
			}
		}
		return null;
	}

	public TraineeDto cgpaToPercentage(TraineeDto dto) {
		PercentageDto percentageDto = dto.getPercentageDto();
		Double sslc = dto.getPercentageDto().getSslcPercentage();
		Double puc = dto.getPercentageDto().getPucPercentage();
		Double degree = dto.getPercentageDto().getDegreePercentage();

		if (percentageDto != null) {
			if (sslc != null && sslc != 0.0) {
				if (sslc <= 9.99) {
					percentageDto.setSslcPercentage((sslc - 0.7) * 10);
				}
			}
			if (puc != null) {
				if (puc <= 9.99 && puc != 0.0) {
					percentageDto.setPucPercentage((puc - 0.7) * 10);
				}
			}
			if (degree != null && degree != 0.0) {
				if (degree <= 9.99) {
					percentageDto.setDegreePercentage((degree - 0.7) * 10);
				}
			}

			dto.setPercentageDto(percentageDto);
		}

		return dto;
	}

	public Predicate<TraineeDto> getBySelection(String courseName, String collegeName, String followupStatus,
			String offeredAs, String yearOfPassOut) {
		Predicate<TraineeDto> predicate = traineeDto -> {
			boolean condition1 = courseName.equals("null")
					|| traineeDto.getCourseInfo().getCourse().equalsIgnoreCase(courseName);
			boolean condition2 = collegeName.equals("null")
					|| traineeDto.getEducationInfo().getCollegeName().equalsIgnoreCase(collegeName);
			boolean condition3 = followupStatus.equals("null")
					|| traineeDto.getFollowupStatus().equalsIgnoreCase(followupStatus);
			boolean condition4 = offeredAs.equals("null")
					|| traineeDto.getCourseInfo().getOfferedAs().equalsIgnoreCase(offeredAs);
			boolean condition5 = yearOfPassOut.equals("null")
					|| traineeDto.getEducationInfo().getYearOfPassout().equalsIgnoreCase(yearOfPassOut);
			return condition1 && condition2 && condition3 && condition4 && condition5;
		};
		return predicate;
	}
	public Predicate<TraineeDto> findBySearchValue(String searchValue, String courseName, String collegeName,
	        String followupStatus, String offeredAs, String yearOfPassOut) {
	    Predicate<TraineeDto> predicate = traineeDto -> searchValue != null &&
	            traineeDto.getBasicInfo().getEmail().equalsIgnoreCase(searchValue);
	    predicate = nullCheck(courseName, collegeName, followupStatus, offeredAs, yearOfPassOut, predicate);
	    return predicate;
	}

	public Predicate<TraineeDto> findSuggestion(String value, String courseName, String collegeName,
	        String followupStatus, String offeredAs, String yearOfPassOut) {
	    Predicate<TraineeDto> predicate = traineeDto -> true;
	    if (value != null && !value.isEmpty()) {
	        String searchValue = value.trim();
	        predicate = predicate.and(traineeDto ->
	                traineeDto.getBasicInfo().getTraineeName().contains(searchValue));
	    }
	    predicate = nullCheck(courseName, collegeName, followupStatus, offeredAs, yearOfPassOut, predicate);

	    return predicate;
	}


	private Predicate<TraineeDto> nullCheck(String courseName, String collegeName, String followupStatus,
			String offeredAs, String yearOfPassOut, Predicate<TraineeDto> predicate) {
		if (!courseName.equals("null")) {
		    predicate = predicate.and(traineeDto -> traineeDto.getCourseInfo().getCourse().equalsIgnoreCase(courseName));
		}
		if (!collegeName.equals("null")) {
		    predicate = predicate.and(traineeDto -> traineeDto.getEducationInfo().getCollegeName().equalsIgnoreCase(collegeName));
		}
		if (!followupStatus.equals("null")) {
		    predicate = predicate.and(traineeDto -> traineeDto.getFollowupStatus().equalsIgnoreCase(followupStatus));
		}
		if (!offeredAs.equals("null")) {
		    predicate = predicate.and(traineeDto -> traineeDto.getCourseInfo().getOfferedAs().equalsIgnoreCase(offeredAs));
		}
		if (!yearOfPassOut.equals("null")) {
		    predicate = predicate.and(traineeDto -> traineeDto.getEducationInfo().getYearOfPassout().equalsIgnoreCase(yearOfPassOut));
		}
		return predicate;
	}
	
}
