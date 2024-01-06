package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import com.xworkz.dream.dto.ClientDataDto;
import com.xworkz.dream.dto.ClientDto;

public interface ClientInformationService {

	String writeClientInformation(ClientDto dto) throws IOException, IllegalAccessException;

	ClientDataDto readClientData(int startingIndex, int maxRows) throws IOException;

	boolean checkComanyName(String companyName) throws IOException;

	ClientDto getClientDtoById(int companyId) throws IOException;

	boolean checkEmail(String companyEmail) throws IOException;

	boolean checkContactNumber(String contactNumber) throws IOException;

	boolean checkCompanyWebsite(String companyWebsite) throws IOException;

	List<ClientDto> getSuggestionDetails(String companyName) throws IOException;

	String updateClientDto(int companyId, ClientDto clientDto) throws IOException, IllegalAccessException;

	List<ClientDto> getDetailsbyCompanyName(String companyName) throws IOException;

}
