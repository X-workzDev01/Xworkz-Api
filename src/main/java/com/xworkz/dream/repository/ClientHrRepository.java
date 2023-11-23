package com.xworkz.dream.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface ClientHrRepository {
	void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException;

	abstract boolean saveClientHrInformation(List<Object> row) throws IOException;
	abstract List<List<Object>> readData() throws IOException;
}
