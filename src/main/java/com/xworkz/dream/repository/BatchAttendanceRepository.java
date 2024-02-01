package com.xworkz.dream.repository;

import java.io.IOException;
import java.util.List;

public interface BatchAttendanceRepository {

	Boolean batchAttendance(List<Object> row, String range) throws IOException;

	List<List<Object>> getBatchAttendanceData(String range) throws IOException;

}
