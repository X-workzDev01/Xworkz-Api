package com.xworkz.dream.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.xworkz.dream.dto.ClientHrDto;

public interface ClientHrRepository {
	void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException;

	abstract boolean saveClientHrInformation(List<Object> row) throws IOException;
	abstract List<ClientHrDto> readData() throws IOException;
	abstract boolean emailCheck(String email) throws IOException;
}
