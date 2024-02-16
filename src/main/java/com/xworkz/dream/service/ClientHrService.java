package com.xworkz.dream.service;

import java.util.List;

import com.xworkz.dream.dto.ClientHrData;
import com.xworkz.dream.dto.ClientHrDto;

public interface ClientHrService {

	String saveClientHrInformation(ClientHrDto clientHrDto);

	ClientHrData readData(int startingIndex, int maxRows, int companyId);

	boolean hrEmailcheck(String companyName);

	List<ClientHrDto> getHrDetailsByCompanyId(int companyId);

	ClientHrDto getHRDetailsByHrId(int hrId);

	String updateHrDetails(int hrId, ClientHrDto clientHrDto);

	boolean hrContactNumberCheck(Long contactNumber);

}
