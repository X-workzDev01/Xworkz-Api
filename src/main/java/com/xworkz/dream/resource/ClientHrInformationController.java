package com.xworkz.dream.resource;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.ClientHrData;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.service.ClientHrService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class ClientHrInformationController {

	private final static Logger log = LoggerFactory.getLogger(ClientHrInformationController.class);

	@Autowired
	private ClientHrService clientHrService;

	@ApiOperation("To save Client information")
	@PostMapping("/registerclienthrinfo")
	public String saveClientHrInformation(@RequestBody ClientHrDto clientHrDto)
			throws IllegalAccessException, IOException {
		log.info("client Hr controller {}", clientHrDto);
		return clientHrService.saveClientHrInformation(clientHrDto);
	}

	@ApiOperation("To save client HR information")
	@GetMapping("/hrdetails")
	public ClientHrData readData(@RequestParam int startingIndex, @RequestParam int maxRows) throws IOException {
		log.debug("Reading client HR information");
		return clientHrService.readData(startingIndex, maxRows);
	}

	@ApiOperation("To check Whether CompanyName is exists or not")
	@GetMapping("/hremailcheck")
	public String checkComanyName(@RequestParam String hrEmail) throws IOException {
		log.info("checking company is already exist or not  {}", hrEmail);
		if (clientHrService.hrEmailcheck(hrEmail)) {
			return "Email already exists.";
		} else {
			return "Email does not exist.";
		}
	}

}
