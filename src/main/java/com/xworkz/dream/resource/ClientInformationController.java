package com.xworkz.dream.resource;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.ClientDataDto;
import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.service.ClientInformationService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class ClientInformationController {

	private static final Logger log = LoggerFactory.getLogger(ClientInformationController.class);

	@Autowired
	private ClientInformationService clientInformationService;

	@ApiOperation("To save client data")
	@PostMapping("/saveclientinfo")
	public ResponseEntity<String> writeClientInformation(@RequestBody ClientDto clientDto) throws IOException, IllegalAccessException {
		log.debug("client information: {}", clientDto);
		String response=clientInformationService.writeClientInformation(clientDto);
		return ResponseEntity.ok(response);
	}

	@ApiOperation("Read client data with pagination")
	@GetMapping("/readclientinfomation")
	public ClientDataDto readClientData(@RequestParam int startingIndex, @RequestParam int maxRows) throws IOException {
		log.info("read client data controller, start index {} and ending  index  {}", startingIndex, maxRows);
		return clientInformationService.readClientData(startingIndex, maxRows);
	}

	@ApiOperation("To check Whether CompanyName is exists or not")
	@GetMapping("/companynamecheck")
	public String checkComanyName(@RequestParam String companyName) throws IOException {
		log.info("checking company is already exist or not  {}",companyName);
		if (clientInformationService.checkComanyName(companyName)) {
			return "Company Already Exists";
		} else {
			return "Company Not Exists";
		}
	}
	@ApiOperation("To get the client details by Id")
	@GetMapping("/getdetailsbyid")
	public ClientDto getClientDtoById(@RequestParam int companyId) throws IOException {
		log.info("get client details by id {}:",companyId);
		return clientInformationService.getClientDtoById(companyId);
	}
}