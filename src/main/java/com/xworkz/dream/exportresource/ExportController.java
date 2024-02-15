package com.xworkz.dream.exportresource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.ExportDto;
import com.xworkz.dream.service.ExportService;

@RestController
@RequestMapping("/export")
public class ExportController {

	@Autowired
	private ExportService service;

	private static final Logger log = LoggerFactory.getLogger(ExportController.class);

	@GetMapping("/getAllData")
	public ResponseEntity<List<ExportDto>> downloadAllData() {
		log.debug("download all data");
		List<ExportDto> listOfExportDto = service.getAllData();
		return ResponseEntity.ok(listOfExportDto);
	}
	
	@GetMapping("/getFilteredData")
	public ResponseEntity<List<ExportDto>> downloadRequiredData(@RequestParam String collegeName,@RequestParam String offeredAs,@RequestParam String yearOfPass,@RequestParam String courseName) {
		log.debug("download all data");
		List<ExportDto> listOfExportDto = service.downloadRequiredData(collegeName,offeredAs,yearOfPass,courseName);
		return ResponseEntity.ok(listOfExportDto);
	}

}
