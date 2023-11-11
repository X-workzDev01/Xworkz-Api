package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

public interface NotificationRepository {
	public List<List<Object>> notification(String spreadsheetId) throws IOException;

}
