package com.xworkz.dream.service.util;

import java.util.List;
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
			if (sslc != null) {
				if (sslc <=9.99) {
					percentageDto.setSslcPercentage((sslc - 0.7) * 10);
				}
			}
			if (puc != null) {
				if (puc <=9.99) {
					percentageDto.setPucPercentage((puc - 0.7) * 10);
				}
			}
			if (degree != null) {
				if (degree <=9.99) {
					percentageDto.setDegreePercentage((degree - 0.7) * 10);
				}
			}

			dto.setPercentageDto(percentageDto);
		}

		return dto;
	}

}
