package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import com.xworkz.dream.dto.ClientHrData;
import com.xworkz.dream.dto.ClientHrDto;

public interface ClientHrService {

	String saveClientHrInformation(ClientHrDto clientHrDto) throws IllegalAccessException, IOException;

	ClientHrData readData(int startingIndex, int maxRows, int companyId) throws IOException;

	boolean hrEmailcheck(String companyName) throws IOException;

	List<ClientHrDto> getHrDetailsByCompanyId(int companyId) throws IOException;
	

}
