package com.xworkz.dream.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.ClientDataDto;
import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.repository.ClientRepository;
import com.xworkz.dream.wrapper.ClientWrapper;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class ClientInformationServiceImpl implements ClientInformationService {

	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private DreamWrapper dreamWrapper;
	@Autowired
	private ClientWrapper clientWrapper;
	@Autowired
	private CacheService cacheService;

	@Value("${login.sheetId}")
	public String sheetId;
	private static final Logger log = LoggerFactory.getLogger(ClientInformationServiceImpl.class);

	@Override
	public String writeClientInformation(ClientDto dto) throws IOException, IllegalAccessException {
		if (dto != null) {
			clientWrapper.setValuesToClientDto(dto);
			List<Object> list = dreamWrapper.extractDtoDetails(dto);
			
			log.info("in client service, Extracted values: {}", list);
			if (clientRepository.writeClientInformation(list)) {
				log.debug("adding newly added data to the cache value is:clientInformation ");
				cacheService.updateCache("clientInformation", sheetId, list);
				return "Client Information saved successfully";
			} else {
				return "Client Information not saved";
			}
		} else {
			return "client information not saved";
		}

	}

	@Override
	public ClientDataDto readClientData(int startingIndex, int maxRows) throws IOException {
		log.debug("start index {} and end index {}", startingIndex, maxRows);
		List<List<Object>> clientData = clientRepository.readData();
		List<ClientDto> sortedDtoList = new ArrayList<ClientDto>();
		if (clientData != null) {
			// sorting data by id and getting pagination data
			sortedDtoList = clientData.stream().map(clientWrapper::listToClientDto)
					.sorted(Comparator.comparing(ClientDto::getId).reversed()).skip(startingIndex).limit(maxRows).collect(Collectors.toList());
		}
		log.debug("sorted data:{}", sortedDtoList);
		return new ClientDataDto(sortedDtoList, clientData.size());
	}

}
