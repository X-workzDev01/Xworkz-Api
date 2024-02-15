package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

public interface NotificationRepository {
	List<List<Object>> notification(String spreadsheetId) throws IOException;

}
