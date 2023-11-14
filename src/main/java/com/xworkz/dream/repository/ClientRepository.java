package com.xworkz.dream.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface ClientRepository {

	void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException;

	abstract boolean writeClientInformation(List<Object> row) throws IOException;
	abstract List<List<Object>> readData() throws IOException;

}
