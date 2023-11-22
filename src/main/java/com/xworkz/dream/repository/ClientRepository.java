package com.xworkz.dream.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.xworkz.dream.dto.ClientDto;

public interface ClientRepository {

	void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException;

	abstract boolean writeClientInformation(List<Object> row) throws IOException;
	abstract List<ClientDto> readData() throws IOException;
	
	abstract boolean checkCompanyName(String companyName) throws IOException;

	abstract ClientDto getClientDtoByCompnayName(String companyName) throws IOException;

	abstract ClientDto getClientDtoById(int companyId) throws IOException;

}
