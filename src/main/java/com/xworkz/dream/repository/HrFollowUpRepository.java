package com.xworkz.dream.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface HrFollowUpRepository {
	abstract void setSheetsService() throws IOException, FileNotFoundException, GeneralSecurityException;

	abstract boolean saveHrFollowUpDetails(List<Object> listitem) throws IOException;

	List<List<Object>> readFollowUpDetailsById() throws IOException;
}
