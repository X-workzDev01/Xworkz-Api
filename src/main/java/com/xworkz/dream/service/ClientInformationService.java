package com.xworkz.dream.service;

import java.util.List;

import com.xworkz.dream.dto.ClientDataDto;
import com.xworkz.dream.dto.ClientDto;

public interface ClientInformationService {

	String writeClientInformation(ClientDto dto);

	ClientDataDto readClientData(int startingIndex, int maxRows);

	boolean checkComanyName(String companyName);

	ClientDto getClientDtoById(int companyId);

	boolean checkEmail(String companyEmail);

	boolean checkContactNumber(String contactNumber);

	boolean checkCompanyWebsite(String companyWebsite);

	List<ClientDto> getSuggestionDetails(String companyName);

	String updateClientDto(int companyId, ClientDto clientDto);

	List<ClientDto> getDetailsbyCompanyName(String companyName);

}
