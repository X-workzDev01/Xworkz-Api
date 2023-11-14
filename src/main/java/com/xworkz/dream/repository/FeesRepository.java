package com.xworkz.dream.repository;

import java.io.IOException;

import com.xworkz.dream.dto.FeesDto;

public interface FeesRepository {
	public String writeFeesDetiles(FeesDto dto) throws IOException;

}
