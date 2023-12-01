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
	private ClientCacheService clientCacheService;

	private static final Logger log = LoggerFactory.getLogger(ClientInformationServiceImpl.class);

	@Override
	public String writeClientInformation(ClientDto dto) throws IOException, IllegalAccessException {
		if (dto != null) {
			clientWrapper.setValuesToClientDto(dto);
			List<Object> list = dreamWrapper.extractDtoDetails(dto);
			log.info("in client service, Extracted values: {}", list);
			if (clientRepository.writeClientInformation(list)) {
				log.debug("adding newly added data to the cache, clientInformation :{}", list);
				clientCacheService.addNewDtoToCache("clientInformation", "ListOfClientDto", dto);
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
		List<List<Object>> listOfData = clientRepository.readData();
		if (listOfData != null) {
			List<ClientDto> ListOfClientDto = listOfData.stream().map(clientWrapper::listToClientDto)
					.sorted(Comparator.comparing(ClientDto::getId, Comparator.reverseOrder()))
					.collect(Collectors.toList());
			List<ClientDto> clientData = ListOfClientDto.stream().skip(startingIndex).limit(maxRows)
					.collect(Collectors.toList());
			return new ClientDataDto(clientData, ListOfClientDto.size());
		} else {
			return null;
		}
	}

	@Override
	public boolean checkComanyName(String companyName) throws IOException {
		log.info("checkComanyName service class " + companyName);
		if (companyName != null) {
			return clientRepository.readData().stream().map(clientWrapper::listToClientDto)
					.anyMatch(clientDto -> companyName.equals(clientDto.getCompanyName()));
		} else {
			return false;
		}
	}

	@Override
	public ClientDto getClientDtoById(int companyId) throws IOException {
		ClientDto clientDto = null;
		if (companyId != 0) {
			clientDto = clientRepository.readData().stream().map(clientWrapper::listToClientDto)
					.filter(ClientDto -> companyId == ClientDto.getId()).findFirst().orElse(null);
		}
		if (clientDto != null) {
			return clientDto;
		}
		return null;
	}
}
