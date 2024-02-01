package com.xworkz.dream.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.xworkz.dream.dto.CsrDto;
import com.xworkz.dream.dto.TraineeDto;

public interface CsrService {

	ResponseEntity<String> validateAndRegister(TraineeDto csrDto, HttpServletRequest request);

	boolean registerCsr(CsrDto csrDto, HttpServletRequest request) throws IOException;

	boolean checkContactNumber(Long contactNumber) throws IOException;

	boolean checkUsnNumber(String usnNumber) throws IOException;

	boolean checkUniqueNumber(String uniqueNumber) throws IOException;

	String generateUniqueID();

}
