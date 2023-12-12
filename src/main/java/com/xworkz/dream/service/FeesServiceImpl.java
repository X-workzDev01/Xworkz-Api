package com.xworkz.dream.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.FeesDto;

@Service
public class FeesServiceImpl implements FeesService {

	@Override
	public String writeFeesDetiles(FeesDto dto) throws IOException {

		return "data update failed ";
	}

}
