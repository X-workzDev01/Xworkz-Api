package com.xworkz.dream.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.ExportDto;
import com.xworkz.dream.dto.PropertiesDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.repository.RegisterRepository;
import com.xworkz.dream.service.util.RegistrationUtil;
import com.xworkz.dream.wrapper.ExportWrapper;

@Service
public class ExportServiceImpl implements ExportService {

	@Autowired
	private RegisterRepository repository;
	@Autowired
	private RegistrationUtil registrationUtil;
	@Autowired
	private ExportWrapper exportWrapper;

	private static final Logger log = LoggerFactory.getLogger(ExportServiceImpl.class);

	@Override
	public List<ExportDto> getAllData() {
		PropertiesDto propertyDto = new PropertiesDto();
		List<List<Object>> listOfData = repository.readData(propertyDto.getId());
		List<ExportDto> exportDto = null;
		if (listOfData != null) {
			List<TraineeDto> listOfTrainee = registrationUtil.readOnlyActiveData(listOfData);
			exportDto = listOfTrainee.stream().map(exportWrapper::assignToExportDto).collect(Collectors.toList());
		} else {
			log.error("listOfData is null:{}", listOfData);
		}
		return exportDto;
	}

	@Override
	public List<ExportDto> downloadRequiredData(String collegeName, String offeredAs, String yearOfPass,
			String courseName) {
		PropertiesDto propertyDto = new PropertiesDto();
		List<List<Object>> listOfData = repository.readData(propertyDto.getId());
		List<ExportDto> exportDto = null;
		if (listOfData != null) {
			List<TraineeDto> traineeDtos = registrationUtil.readOnlyActiveData(listOfData);
			return exportDto = filterTraineeData(traineeDtos, collegeName, offeredAs, yearOfPass, courseName);
		} else {
			return exportDto;
		}
	}

	private List<ExportDto> filterTraineeData(List<TraineeDto> traineeDtos, String collegeName, String offeredAs,
			String yearOfPass, String courseName) {
		log.info("Export by collegeName:{},offeredAs:{},yearOfPass:{}, courseName:{}",collegeName,offeredAs,yearOfPass,courseName);
		return traineeDtos.stream()
				.filter(traineeDto -> collegeName.equalsIgnoreCase(ServiceConstant.NULL.toString())
						|| traineeDto.getEducationInfo().getCollegeName().equalsIgnoreCase(collegeName))
				.filter(traineeDto -> offeredAs.equalsIgnoreCase(ServiceConstant.NULL.toString())
						|| traineeDto.getCourseInfo().getOfferedAs().equalsIgnoreCase(offeredAs))
				.filter(traineeDto -> yearOfPass.equalsIgnoreCase(ServiceConstant.NULL.toString())
						|| traineeDto.getEducationInfo().getYearOfPassout().equalsIgnoreCase(yearOfPass))
				.filter(traineeDto -> courseName.equalsIgnoreCase(ServiceConstant.NULL.toString())
						|| traineeDto.getCourseInfo().getCourse().equalsIgnoreCase(courseName))
				.map(exportWrapper::assignToExportDto).collect(Collectors.toList());
	}

}
