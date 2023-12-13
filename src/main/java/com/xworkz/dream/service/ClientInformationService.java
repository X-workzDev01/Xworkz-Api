package com.xworkz.dream.service;

import java.io.IOException;

import com.xworkz.dream.dto.ClientDataDto;
import com.xworkz.dream.dto.ClientDto;

public interface ClientInformationService {

	abstract String writeClientInformation(ClientDto dto) throws IOException, IllegalAccessException;
	abstract ClientDataDto readClientData(int startingIndex,int maxRows) throws IOException;
	abstract boolean checkComanyName(String companyName) throws IOException;
	abstract ClientDto getClientDtoById(int companyId) throws IOException;
	abstract boolean checkEmail(String companyEmail) throws IOException;
	abstract boolean updateClientDetails(ClientDto clientDto);
}
