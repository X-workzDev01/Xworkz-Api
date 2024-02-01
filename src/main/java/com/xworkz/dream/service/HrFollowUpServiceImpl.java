package com.xworkz.dream.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.cache.ClientCacheService;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.HrFollowUpDto;
import com.xworkz.dream.repository.ClientHrRepository;
import com.xworkz.dream.repository.HrFollowUpRepository;
import com.xworkz.dream.util.ClientInformationUtil;
import com.xworkz.dream.wrapper.ClientWrapper;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class HrFollowUpServiceImpl implements HrFollowUpService {

	@Autowired
	private HrFollowUpRepository hrFollowUpRepository;
	@Autowired
	private ClientInformationUtil clientInformationUtil;
	@Autowired
	private DreamWrapper dreamWrapper;
	@Autowired
	private ClientHrRepository clientHrRepository;
	@Autowired
	private ClientCacheService clientCacheService;
	@Autowired
	private ClientWrapper clientWrapper;

	private static final Logger log = LoggerFactory.getLogger(HrFollowUpServiceImpl.class);

	@Override
	public String saveHrFollowUpDetails(HrFollowUpDto dto) throws IllegalAccessException, IOException {
		log.info("saveHrFollowUpDetails hr follow Up Dto: {}", dto);
		clientInformationUtil.settingNaValues(dto);
		List<Object> list = dreamWrapper.extractDtoDetails(dto);
		if (dto != null) {
			if (hrFollowUpRepository.saveHrFollowUpDetails(list)) {
				clientCacheService.addHRDetailsToCache("hrFollowUpDetails","hrFollowUp",list);
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
			List<HrFollowUpDto> listOfHrFollowUpDto = listOfData.stream().map(clientWrapper::listToHrFollowUpDto)
					.filter(HrFollowUpDto -> HrFollowUpDto.getHrId().equals(hrId))
					.sorted(Comparator.comparing(HrFollowUpDto::getId).reversed()).collect(Collectors.toList());
			return listOfHrFollowUpDto;
		} else {
			return null;
		}
	}

	@Override
	public List<HrFollowUpDto> getFollowUpDetails(Integer companyId) throws IOException {
		log.info("get follow up details in service");
		if (companyId != null) {
			List<List<Object>> listOfHr = clientHrRepository.readData();
			List<List<Object>> listOfFollowUpData = hrFollowUpRepository.readFollowUpDetailsById();

			if (listOfHr != null && listOfFollowUpData != null) {
				List<ClientHrDto> listOfHrDto = listOfHr.stream().map(clientWrapper::listToClientHrDto)
						.filter(hrDetails -> hrDetails.getCompanyId().equals(companyId)).collect(Collectors.toList());

				  List<HrFollowUpDto> hrFollowUpList = listOfFollowUpData.stream()
		                    .map(clientWrapper::listToHrFollowUpDto)
		                    .filter(followUpDto ->
		                            listOfHrDto.stream()
		                                    .anyMatch(dto -> followUpDto.getHrId().equals(dto.getId())))
		                    .sorted(Comparator.comparing(HrFollowUpDto::getId).reversed())
		                    .collect(Collectors.toList());
		            return hrFollowUpList;
				}
		}
		return null;
	}

}
