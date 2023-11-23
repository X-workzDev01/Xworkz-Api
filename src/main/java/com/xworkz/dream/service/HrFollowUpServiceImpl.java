package com.xworkz.dream.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.HrFollowUpDto;
import com.xworkz.dream.repository.HrFollowUpRepository;
import com.xworkz.dream.wrapper.DreamWrapper;
import com.xworkz.dream.wrapper.HrFollowUpWrapper;

@Service
public class HrFollowUpServiceImpl implements HrFollowUpService {

	@Autowired
	private HrFollowUpRepository hrFollowUpRepository;

	@Autowired
	private HrFollowUpWrapper hrFollowUpWrapper;
	@Autowired
	private DreamWrapper dreamWrapper;

	private static final Logger log = LoggerFactory.getLogger(HrFollowUpServiceImpl.class);

	@Override
	public String saveHrFollowUpDetails(HrFollowUpDto dto) throws IllegalAccessException, IOException {
		log.info("saveHrFollowUpDetails hr follow Up Dto: {}", dto);
		hrFollowUpWrapper.settingNaValues(dto);
		List<Object> list = dreamWrapper.extractDtoDetails(dto);
		if (dto != null) {
			if (hrFollowUpRepository.saveHrFollowUpDetails(list)) {
				return "Hr Follow up details saved successfully";
			} else {
				return "Hr Follow up details not saved";
			}
		}
		return "Hr Follow up details not saved";
	}

	@Override
	public List<HrFollowUpDto> getHrFollowUpDetailsBy(int hrId) throws IOException {
		List<List<Object>> listOfData = hrFollowUpRepository.readFollowUpDetailsById();
		if (listOfData != null) {
			List<HrFollowUpDto> listOfHrFollowUpDto = listOfData.stream().map(hrFollowUpWrapper::listToHrFollowUpDto)
					.filter(HrFollowUpDto -> HrFollowUpDto.getHrId().equals(hrId))
					.sorted(Comparator.comparing(HrFollowUpDto::getId)).collect(Collectors.toList());
			return listOfHrFollowUpDto;
		} else {
			return null;
		}
	}

}
