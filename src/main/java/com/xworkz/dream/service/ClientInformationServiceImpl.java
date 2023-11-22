package com.xworkz.dream.service;

import java.io.IOException;
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
		int size = clientRepository.readData().size();
		List<ClientDto> clientData = clientRepository.readData().stream().skip(startingIndex).limit(maxRows)
				.collect(Collectors.toList());
		return new ClientDataDto(clientData, size);
	}

	@Override
	public boolean checkComanyName(String companyName) throws IOException {
		log.info("checkComanyName service class " + companyName);
		if (companyName != null) {
			return clientRepository.checkCompanyName(companyName);
		} else {
			return false;
		}
	}

	@Override
	public ClientDto getClientDtoById(int companyId) throws IOException {
		ClientDto clientDto = null;
		if (companyId != 0) {
			clientDto = clientRepository.getClientDtoById(companyId);
		}
		if (clientDto != null) {
			return clientDto;
		}
		return null;
	}
}
