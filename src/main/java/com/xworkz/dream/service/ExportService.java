package com.xworkz.dream.service;

import java.util.List;

import com.xworkz.dream.dto.ExportDto;

public interface ExportService {

	List<ExportDto> getAllData();

	List<ExportDto> downloadRequiredData(String collegeName, String offeredAs, String yearOfPass, String courseName);

}
