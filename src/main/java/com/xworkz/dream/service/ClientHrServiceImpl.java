package com.xworkz.dream.service;

import java.io.IOException;
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
import com.xworkz.dream.dto.ClientHrData;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.repository.ClientHrRepository;
import com.xworkz.dream.util.ClientInformationUtil;
import com.xworkz.dream.wrapper.ClientWrapper;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class ClientHrServiceImpl implements ClientHrService {
	@Value("${sheets.hrStartRow}")
	private String hrStartRow;
	@Value("${sheets.hrEndRow}")
	private String hrEndRow;
	@Value("${sheets.hrSheetName}")
	private String hrSheetName;

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
	public String saveClientHrInformation(ClientHrDto clientHrDto) throws IllegalAccessException, IOException {
		log.info("ClientHr Service");
		if (clientHrDto != null) {

			clientInformationUtil.setValuesToClientHrDto(clientHrDto);
			log.debug("Received ClientHrDto: {}", clientHrDto);
			List<Object> listItem = dreamWrapper.extractDtoDetails(clientHrDto);

			if (clientHrRepository.saveClientHrInformation(listItem)) {
				// clientCacheService.addHRDetailsToCache("hrDetails", "listofHRDetails",
				// listItem);
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
	public ClientHrData readData(int startingIndex, int maxRows, int companyId) throws IOException {
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
	public boolean hrEmailcheck(String hrEmail) throws IOException {
		if (hrEmail != null) {
			return clientHrRepository.readData().stream().map(clientWrapper::listToClientHrDto)
					.anyMatch(clientHrDto -> hrEmail.equals(clientHrDto.getHrEmail()));
		} else {
			return false;
		}
	}

	@Override
	public List<ClientHrDto> getHrDetailsByCompanyId(int companyId) throws IOException {
		log.info("get details by companyId, {}", companyId);
		List<ClientHrDto> listofClientHr = clientHrRepository.readData().stream().map(clientWrapper::listToClientHrDto)
				.filter(clientHrDto -> clientHrDto.getCompanyId() == companyId).collect(Collectors.toList());
		return listofClientHr;
	}

	@Override
	public ClientHrDto getHRDetailsByHrId(int hrId) throws IOException {
		ClientHrDto hrDto = null;
		if (hrId != 0) {
			return hrDto = clientHrRepository.readData().stream().map(clientWrapper::listToClientHrDto)
					.filter(ClientHrDto -> hrId == ClientHrDto.getId()).findFirst().orElse(hrDto);
		}
		return hrDto;
	}

	@Override
	public boolean hrContactNumberCheck(Long contactNumber) throws IOException {
		log.info("checking contact number, {}", contactNumber);
		List<List<Object>> listOfHrDetails = clientHrRepository.readData();
		if (contactNumber != null) {
			if (listOfHrDetails != null) {
				return listOfHrDetails.stream().map(clientWrapper::listToClientHrDto)
						.anyMatch(clientHrDto-> contactNumber.equals(clientHrDto.getHrContactNumber()));
			}
		}
		return false;
	}

	@Override
	public String updateHrDetails(int hrId, ClientHrDto clientHrDto) throws IllegalAccessException, IOException {
		int rowNumber = hrId + 1;
		String range = hrSheetName + hrStartRow + rowNumber + ":" + hrEndRow + rowNumber;
		if (hrId != 0 && clientHrDto != null) {
			AuditDto auditDto = new AuditDto();
			auditDto.setUpdatedOn(LocalDateTime.now().toString());
			clientHrDto.getAdminDto().setUpdatedOn(LocalDateTime.now().toString());
			System.out.println("updated values:" + clientHrDto);
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
				return "updated Successfully";
			} else {
				return "not updated successfully";
			}
		}
		return null;
	}

}
