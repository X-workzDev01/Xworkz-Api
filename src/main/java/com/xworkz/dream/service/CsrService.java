package com.xworkz.dream.service;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.CsrDto;
import com.xworkz.dream.dto.TraineeDto;

public interface CsrService {

	ResponseEntity<String> validateAndRegister(TraineeDto csrDto);

	boolean registerCsr(CsrDto csrDto);

	boolean checkContactNumber(Long contactNumber) ;

	boolean checkUsnNumber(String usnNumber);

	boolean checkUniqueNumber(String uniqueNumber);

	String generateUniqueID();

}
