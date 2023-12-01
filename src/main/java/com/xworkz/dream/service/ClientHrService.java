package com.xworkz.dream.service;

import java.io.IOException;

import com.xworkz.dream.dto.ClientHrData;
import com.xworkz.dream.dto.ClientHrDto;

public interface ClientHrService {
	
	abstract String saveClientHrInformation(ClientHrDto clientHrDto) throws IllegalAccessException, IOException;

	abstract ClientHrData readData(int startingIndex,int maxRows) throws IOException;

	abstract boolean hrEmailcheck(String companyName) throws IOException;

}
