package com.xworkz.dream.wrapper;

import com.xworkz.dream.dto.ExportDto;
import com.xworkz.dream.dto.TraineeDto;

public interface ExportWrapper {

	ExportDto assignToExportDto(TraineeDto dto);
}
