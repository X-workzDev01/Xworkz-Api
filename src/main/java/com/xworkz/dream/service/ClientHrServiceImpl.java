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
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.xworkz.dream.cache.ClientCacheService;
import com.xworkz.dream.clientDtos.ClientValueDto;
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.ClientHrData;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.PropertiesDto;
import com.xworkz.dream.repository.ClientHrRepository;
import com.xworkz.dream.util.ClientInformationUtil;
import com.xworkz.dream.wrapper.ClientWrapper;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class ClientHrServiceImpl implements ClientHrService {
	@Autowired
	private PropertiesDto propertiesDto;
	@Autowired
	private ClientHrRepository clientHrRepository;
	@Autowired
	private DreamWrapper dreamWrapper;
	@Autowired
	private ClientWrapper clientWrapper;
	@Autowired
	private ClientCacheService clientCacheService;
	@Autowired
	private ClientInformationUtil clientInformationUtil;

	private final static Logger log = LoggerFactory.getLogger(ClientHrServiceImpl.class);

	@Override
	public String saveClientHrInformation(ClientHrDto clientHrDto) {
		if (clientHrDto != null) {
			clientInformationUtil.setValuesToClientHrDto(clientHrDto);
			log.debug("Received ClientHrDto: {}", clientHrDto);
			List<Object> listItem = new ArrayList<>();
			try {
				listItem = dreamWrapper.extractDtoDetails(clientHrDto);
			} catch (IllegalAccessException e) {
				log.error("Exception in save client information,{}", e.getMessage());
			}

			if (clientHrRepository.saveClientHrInformation(listItem)) {
				clientCacheService.addHRDetailsToCache("hrDetails", "listofHRDetails", listItem);
				clientCacheService.addToCache("getListOfHrEmail", "listOfHrEmail", clientHrDto.getHrEmail());
				clientCacheService.addToCache("getListOfContactNumber", "listOfHrContactNumber",
						clientHrDto.getHrContactNumber().toString());
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
	public ClientHrData readData(int startingIndex, int maxRows, int companyId) {
		log.info("get data from client hr");
		int size = clientHrRepository.readData().size();
		if (companyId != 0) {
			List<ClientHrDto> listOfDto = getHrDetailsByCompanyId(companyId);
			return new ClientHrData(listOfDto, listOfDto.size());

		} else {
			List<ClientHrDto> sortedData = clientHrRepository.readData().stream().map(clientWrapper::listToClientHrDto)
					.sorted(Comparator.comparing(ClientHrDto::getId, Comparator.reverseOrder()))
					.collect(Collectors.toList());

			List<ClientHrDto> listOfClientHrDto = sortedData.stream().skip(startingIndex).limit(size)
					.collect(Collectors.toList());

			return new ClientHrData(listOfClientHrDto, sortedData.size());
		}
	}

	@Override
	public boolean hrEmailcheck(String hrEmail) {
		if (hrEmail != null && !hrEmail.isEmpty()) {
			List<List<Object>> listOfEmail = clientHrRepository.getListOfHrEmails();
			if (listOfEmail != null) {
				ClientValueDto clientValueDto = listOfEmail
						.stream().map(clientWrapper::listToClientValueDto).filter(clientDto -> clientDto != null
								&& clientDto.getMapValue() != null && clientDto.getMapValue().equalsIgnoreCase(hrEmail))
						.findFirst().orElse(null);
				if (clientValueDto != null) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public List<ClientHrDto> getHrDetailsByCompanyId(int companyId) {
		log.info("get details by companyId, {}", companyId);
		List<ClientHrDto> listofClientHr = clientHrRepository.readData().stream().map(clientWrapper::listToClientHrDto)
				.filter(clientHrDto -> clientHrDto.getCompanyId() != null && clientHrDto.getCompanyId() == companyId)
				.collect(Collectors.toList());
		return listofClientHr;
	}

	@Override
	public ClientHrDto getHRDetailsByHrId(int hrId) {
		ClientHrDto hrDto = null;
		if (hrId != 0) {
			return hrDto = clientHrRepository.readData().stream().map(clientWrapper::listToClientHrDto)
					.filter(ClientHrDto -> ClientHrDto.getId() != null && hrId == ClientHrDto.getId()).findFirst()
					.orElse(hrDto);
		}
		return hrDto;
	}

	@Override
	public boolean hrContactNumberCheck(Long contactNumber) {
		log.info("checking contact number, {}", contactNumber);
		List<List<Object>> listOfContactNumber = clientHrRepository.getListOfHrContactNumber();
		if (contactNumber != null) {
			if (listOfContactNumber != null) {
				ClientValueDto clientValueDto = listOfContactNumber.stream().map(clientWrapper::listToClientValueDto)
						.filter(clientDto -> clientDto != null && clientDto.getMapValue() != null
								&& clientDto.getMapValue().equals(contactNumber.toString()))
						.findFirst().orElse(null);
				if (clientValueDto != null) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	@Override
	public String updateHrDetails(int hrId, ClientHrDto clientHrDto) {
	    int rowNumber = hrId + 1;
	    String range = propertiesDto.getHrSheetName() + propertiesDto.getHrStartRow() + rowNumber + ":"
	            + propertiesDto.getHrEndRow() + rowNumber;
	    if (hrId != 0 && clientHrDto != null) {
	        ClientHrDto hrDto = getHRDetailsByHrId(hrId);
	        AuditDto auditDto = new AuditDto();
	        auditDto.setUpdatedOn(LocalDateTime.now().toString());
	        clientHrDto.getAdminDto().setUpdatedOn(LocalDateTime.now().toString());
	        try {
	            List<List<Object>> values = Arrays.asList(dreamWrapper.extractDtoDetails(clientHrDto));
	            if (!values.isEmpty()) {
	                List<Object> modifiedValues = new ArrayList<>(values.get(0).subList(1, values.get(0).size()));
	                values.set(0, modifiedValues);
	                log.debug("values {}", values);
	            }
	            ValueRange valueRange = new ValueRange();
	            valueRange.setValues(values);
	            UpdateValuesResponse updated = clientHrRepository.updateHrDetails(range, valueRange);
	            log.info("update response is :{}", updated);
	            if (updated != null) {
	                List<List<Object>> listOfItems = null;
	                try {
	                    listOfItems = Arrays.asList(dreamWrapper.extractDtoDetails(clientHrDto));
	                    log.info("{}", listOfItems);
	                    clientCacheService.updateHrDetailsInCache("hrDetails", "listofHRDetails", listOfItems);
	                    updateDataToCache(clientHrDto, hrDto);
	                } catch (IllegalAccessException e) {
	                    log.error("Exception update HR,{}", e.getMessage());
	                }
	                return "updated Successfully";
	            } else {
	                return "not updated successfully";
	            }
	        } catch (Exception e) {
	            log.error("Exception update HR,{}", e.getMessage());
	            return "not updated successfully";
	        }
	    }
	    return null;
	}

	private void updateDataToCache(ClientHrDto clientHrDto, ClientHrDto hrDto) {
		if (hrDto != null && clientHrDto != null) {
		    if (!hrDto.getHrEmail().equalsIgnoreCase(clientHrDto.getHrEmail())) {
		        clientCacheService.updateCache("getListOfHrEmail", "listOfHrEmail", hrDto.getHrEmail(),
		                clientHrDto.getHrEmail());
		    }
		    if (!hrDto.getHrContactNumber().equals(clientHrDto.getHrContactNumber())) {
		        clientCacheService.updateCache("getListOfContactNumber", "listOfHrContactNumber",
		                hrDto.getHrContactNumber().toString(), clientHrDto.getHrContactNumber().toString());
		    }
		}
	}

}
