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
import com.xworkz.dream.dto.ClientHrData;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.repository.ClientHrRepository;
import com.xworkz.dream.wrapper.ClientHrWrapper;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class ClientHrServiceImpl implements ClientHrService {

	@Autowired
	private ClientHrRepository clientHrRepository;
	@Autowired
	private DreamWrapper dreamWrapper;
	@Autowired
	private ClientHrWrapper clientHrWrapper;
	@Autowired
	private ClientCacheService clientCacheService;

	private final static Logger log = LoggerFactory.getLogger(ClientHrServiceImpl.class);

	@Override
	public String saveClientHrInformation(ClientHrDto clientHrDto) throws IllegalAccessException, IOException {
		log.info("ClientHr Service");
		if (clientHrDto != null) {
			clientHrWrapper.setValuesToClientHrDto(clientHrDto);
			log.debug("Received ClientHrDto: {}", clientHrDto);
			List<Object> listItem = dreamWrapper.extractDtoDetails(clientHrDto);

			if (clientHrRepository.saveClientHrInformation(listItem)) {
				clientCacheService.addHRDetailsToCache("hrDetails", "listofHRDetails", listItem);
				log.info("Client HR information saved successfully");
				return "Client HR information saved successfully";
			} else {
				log.error("Failed to save Client HR information");
				return "Failed to save Client HR information";
			}
		} else {
			log.warn("ClientHrDto is null. Cannot save Client HR information.");
			return "ClientHrDto is null. Cannot save Client HR information.";
		}
	}

	@Override
	public ClientHrData readData(int startingIndex, int maxRows) throws IOException {

		int size = clientHrRepository.readData().size();
		List<ClientHrDto> sortedData = clientHrRepository.readData().stream().map(clientHrWrapper::listToClientHrDto)
				.sorted(Comparator.comparing(ClientHrDto::getId, Comparator.reverseOrder()))
				.collect(Collectors.toList());

		List<ClientHrDto> listOfClientHrDto = sortedData.stream().skip(startingIndex).limit(size)
				.collect(Collectors.toList());

		return new ClientHrData(listOfClientHrDto, sortedData.size());
	}

	@Override
	public boolean hrEmailcheck(String hrEmail) throws IOException {
		if (hrEmail != null) {
			return clientHrRepository.readData().stream().map(clientHrWrapper::listToClientHrDto)
					.anyMatch(clientHrDto -> hrEmail.equals(clientHrDto.getHrEmail()));
		} else {
			return false;
		}
	}
}
