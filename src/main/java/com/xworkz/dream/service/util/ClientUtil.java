package com.xworkz.dream.service.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.HrFollowUpDto;
import com.xworkz.dream.repository.ClientHrRepository;
import com.xworkz.dream.repository.ClientRepository;
import com.xworkz.dream.repository.HrFollowUpRepository;
import com.xworkz.dream.wrapper.ClientWrapper;

@Component
public class ClientUtil {

	@Autowired
	private ClientWrapper clientWrapper;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private ClientHrRepository clientHrRepository;
	@Autowired
	private HrFollowUpRepository hrFollowUpRepository;

	private static final Logger log = LoggerFactory.getLogger(ClientUtil.class);

	public List<ClientDto> getActiveClientRecords() {
		List<List<Object>> listOfData = clientRepository.readData();
		System.out.println("client data:"+listOfData);
		if (listOfData != null) {
			return listOfData.stream().map(clientWrapper::listToClientDto)
					.filter(clientDto -> clientDto != null&&clientDto.getId()!=null
							&& clientDto.getStatus()!=null &&clientDto.getStatus().equalsIgnoreCase(ServiceConstant.ACTIVE.toString()))
					.sorted(Comparator.comparing(ClientDto::getId, Comparator.reverseOrder()))
					.collect(Collectors.toList());
		} else {
			log.info("client information is not present,{}", listOfData);
			return Collections.emptyList();
		}
	}

//	public List<ClientHrDto> readHrDetails() {
//	    List<List<Object>> listOfHrDetails = clientHrRepository.readData();
//	    System.err.println("Hr Details:"+listOfHrDetails);
//	    if(listOfHrDetails != null && !listOfHrDetails.isEmpty()) {
//	        return listOfHrDetails.stream()
//	                .filter(Objects::nonNull) 
//	                .map(clientWrapper::listToClientHrDto)
//	                .filter(Objects::nonNull) 
//	                .sorted((c1, c2) -> {
//	                    if (c1.getId() == null && c2.getId() == null) {
//	                        return 0;
//	                    } else if (c1.getId() == null) {
//	                        return -1;
//	                    } else if (c2.getId() == null) {
//	                        return 1;
//	                    }
//	                    return c2.getId().compareTo(c1.getId());
//	                })
//	                .collect(Collectors.toList());
//	    } else {
//	        log.info("client HR information is not present, {}", listOfHrDetails);
//	        return Collections.emptyList();
//	    }
//	}
	public List<ClientHrDto> readHrDetails() {
		List<List<Object>> listOfHrDetails = clientHrRepository.readData();
		if(listOfHrDetails != null && !listOfHrDetails.isEmpty()) {
		return listOfHrDetails.stream().map(clientWrapper::listToClientHrDto)
				.sorted(Comparator.comparing(ClientHrDto::getId, Comparator.reverseOrder()))
				.collect(Collectors.toList());
		}else {
			log.info("client HR information is not present,{}", listOfHrDetails);
			return Collections.emptyList();
		}
	}

	
	public List<HrFollowUpDto> readClientFollowUp(){
		List<List<Object>> listOfFollowUp=hrFollowUpRepository.readFollowUpDetailsById();
		if(listOfFollowUp!=null) {
		return	listOfFollowUp.stream().map(clientWrapper::listToHrFollowUpDto)
			.sorted(Comparator.comparing(HrFollowUpDto::getId, Comparator.reverseOrder()))
			.collect(Collectors.toList());
		}else {
			log.info("client follow information is not present,{}", listOfFollowUp);
			return Collections.emptyList();
		}
	}
}
