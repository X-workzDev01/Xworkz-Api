package com.xworkz.dream.service.util;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.wrapper.DreamWrapper;

@Component
public class RegistrationUtil {

	@Autowired
	private DreamWrapper wrapper;

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

}
