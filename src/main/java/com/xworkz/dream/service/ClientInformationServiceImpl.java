package com.xworkz.dream.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.cache.ClientCacheService;
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.ClientDataDto;
import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.repository.ClientRepository;
import com.xworkz.dream.util.ClientInformationUtil;
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

	@Value("${sheets.clientStartRow}")
	private String clientStartRow;
	@Value("${sheets.clientEndRow}")
	private String clientEndRow;
	@Value("${sheets.clientSheetName}")
	private String clientSheetName;

	@Autowired
	private ClientInformationUtil clientInformationUtil;

	private static final Logger log = LoggerFactory.getLogger(ClientInformationServiceImpl.class);

	@Override
	public String writeClientInformation(ClientDto dto) {
		if (dto != null) {
			clientInformationUtil.setValuesToClientDto(dto);
			try {
				List<Object> list = dreamWrapper.extractDtoDetails(dto);

				log.info("in client service, Extracted values: {}", list);
				if (clientRepository.writeClientInformation(list)) {
					log.debug("adding newly added data to the cache, clientInformation :{}", list);

					clientCacheService.addNewDtoToCache("clientInformation", "listOfClientDto", list);
					return "Client Information saved successfully";
				}
			} catch (IllegalAccessException e) {
				log.error("Exception in writeClient,{}", e.getMessage());
				return "Client Information not saved successfully";
			}
		}
		return "Client Information not saved successfully";
	}

	@Override
	public ClientDataDto readClientData(int startingIndex, int maxRows) {
		log.debug("start index {} and end index {}", startingIndex, maxRows);
		List<List<Object>> listOfData = clientRepository.readData();

		if (listOfData != null) {
			List<ClientDto> ListOfClientDto = listOfData.stream().map(clientWrapper::listToClientDto)
					.filter(dto -> dto.getStatus() != null && !dto.getStatus().equalsIgnoreCase("InActive"))
					.sorted(Comparator.comparing(ClientDto::getId, Comparator.reverseOrder()))
					.collect(Collectors.toList());
			List<ClientDto> clientData = ListOfClientDto.stream().skip(startingIndex).limit(maxRows)
					.collect(Collectors.toList());
			return new ClientDataDto(clientData, listOfData.size());
		} else {

			return new ClientDataDto(null, 0);
		}
	}

	@Override
	public boolean checkComanyName(String companyName) {
		log.info("checkComanyName service class {} ",companyName);

		List<List<Object>> listOfData = clientRepository.readData();
		if (companyName != null) {
			if (listOfData != null) {
				return listOfData.stream().map(clientWrapper::listToClientDto)
						.anyMatch(clientDto -> companyName.equalsIgnoreCase(clientDto.getCompanyName()));
			}
		}
		return false;
	}

	@Override
	public ClientDto getClientDtoById(int companyId) {
		ClientDto clientDto = null;
		log.info("find the dto by id");
		if (companyId != 0) {
			clientDto = clientRepository.readData().stream().map(clientWrapper::listToClientDto)
					.filter(ClientDto -> companyId == ClientDto.getId()).findFirst().orElse(null);
		}
		if (clientDto != null) {
			return clientDto;
		}
		return clientDto;
	}

	@Override
	public boolean checkEmail(String companyEmail) {
		log.info("checking company Email: {}", companyEmail);
		List<List<Object>> listOfData = clientRepository.readData();
		if (companyEmail != null) {
			if (listOfData != null) {
				return listOfData.stream().map(clientWrapper::listToClientDto)
						.anyMatch(clientDto -> companyEmail.equalsIgnoreCase(clientDto.getCompanyEmail()));
			}
		}
		return false;
	}

	@Override
	public boolean checkContactNumber(String contactNumber) {
		log.info("checking company contactNumber: {}", contactNumber);
		List<List<Object>> listOfData = clientRepository.readData();
		if (contactNumber != null) {
			if (listOfData != null) {
				Long companyLandLineNumber = Long.parseLong(contactNumber);
				return listOfData.stream().map(clientWrapper::listToClientDto)
						.anyMatch(clientDto -> companyLandLineNumber.equals(clientDto.getCompanyLandLineNumber()));
			}
		}
		return false;
	}

	@Override
	public boolean checkCompanyWebsite(String companyWebsite) {
		log.info("checking company ompanyWebsite: {}", companyWebsite);
		List<List<Object>> listOfData = clientRepository.readData();
		if (companyWebsite != null) {
			if (listOfData != null) {
				return listOfData.stream().map(clientWrapper::listToClientDto)
						.anyMatch(clientDto -> companyWebsite.equalsIgnoreCase(clientDto.getCompanyWebsite()));
			}
		}
		return false;
	}

	@Override
	public List<ClientDto> getSuggestionDetails(String companyName) {
		log.info("get the suggestion details by companyName :{}", companyName);
		List<ClientDto> suggestionList = new ArrayList<ClientDto>();
		List<List<Object>> listOfData = clientRepository.readData();
		if (companyName != null) {
			if (listOfData != null) {

				return suggestionList = listOfData.stream().map(clientWrapper::listToClientDto).filter(
						clientDto -> clientDto.getCompanyName().toLowerCase().contains(companyName.toLowerCase()))
						.collect(Collectors.toList());
			}
			log.info("suggestionList is, {}", suggestionList);
		}
		return suggestionList;
	}

	@Override
	public List<ClientDto> getDetailsbyCompanyName(String companyName) {
		List<ClientDto> clientDto = null;
		List<List<Object>> listofDtos = clientRepository.readData();
		if (companyName != null) {
			if (listofDtos != null) {
				clientDto = listofDtos.stream().map(clientWrapper::listToClientDto)
						.filter(ClientDto -> ClientDto.getCompanyName().equalsIgnoreCase(companyName))
						.collect(Collectors.toList());
				log.info("returned company dto is, {}", clientDto);
			}
		}
		if (clientDto != null) {
			return clientDto;
		} else {
			return clientDto;
		}
	}

	@Override
	public String updateClientDto(int companyId, ClientDto clientDto) {
		log.info("updating client dto {}, Id {}", clientDto, companyId);
		String range = clientSheetName + clientStartRow + (companyId + 1) + ":" + clientEndRow + (companyId + 1);

		if (companyId != 0 && clientDto != null) {
			AuditDto auditDto = new AuditDto();
			auditDto.setUpdatedOn(LocalDateTime.now().toString());
			clientDto.getAdminDto().setUpdatedOn(auditDto.getUpdatedOn());
			List<List<Object>> values;
			try {
				values = Arrays.asList(dreamWrapper.extractDtoDetails(clientDto));

				if (!values.isEmpty()) {
					List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
					modifiedValues.remove(0);
					values.set(0, modifiedValues);
					log.debug("values {}", values);
				}
				ValueRange valueRange = new ValueRange();
				valueRange.setValues(values);
				UpdateValuesResponse updated = clientRepository.updateclientInfo(range, valueRange);
				log.info("update response is :{}", updated);
				if (updated != null) {
					List<List<Object>> listOfItems = Arrays.asList(dreamWrapper.extractDtoDetails(clientDto));
					clientCacheService.updateClientDetailsInCache("clientInformation", "listOfClientDto", listOfItems);
					return "updated Successfully";
				} else {
					return "not updated successfully";
				}
			} catch (Exception e) {
				log.error("Exception in updateClientDto,{}", e.getMessage());
			}
		}
		return "not updated successfully";
	}

}
