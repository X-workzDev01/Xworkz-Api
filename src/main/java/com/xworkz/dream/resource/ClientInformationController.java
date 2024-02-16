package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	@PostMapping("/registerclient")
	public ResponseEntity<String> writeClientInformation(@RequestBody ClientDto clientDto){
		String response = clientInformationService.writeClientInformation(clientDto);
		return ResponseEntity.ok(response);
	}

	@ApiOperation("Read client data with pagination")
	@GetMapping("/readclientinfomation")
	public ClientDataDto readClientData(@RequestParam int startingIndex, @RequestParam int maxRows){
		log.info("read client data controller, start index {} and ending  index  {}", startingIndex, maxRows);
		return clientInformationService.readClientData(startingIndex, maxRows);
	}

	@ApiOperation("To check Whether CompanyName is exists or not")
	@GetMapping("/companynamecheck")
	public String checkComanyName(@RequestParam String companyName) {
		log.info("checking company is already exist or not  {}", companyName);
		if (clientInformationService.checkComanyName(companyName)) {
			return "Company Already Exists";
		} else {
			return "Company Not Exists";
		}
	}

	@ApiOperation("To get the client details by Id")
	@GetMapping("/getdetailsbyid")
	public ClientDto getClientDtoById(@RequestParam int companyId){
		log.info("get client details by id {}:", companyId);
		return clientInformationService.getClientDtoById(companyId);
	}

	@ApiOperation("To check the email Id of Company")
	@GetMapping("/checkcompanyemail")
	public String checkEmail(@RequestParam String companyEmail)  {
		log.info("checking company Email exist of not email is:{}", companyEmail);
		if (clientInformationService.checkEmail(companyEmail)) {
			return "Company Email Already Exists";
		} else {
			return "Company Email Not Exists";
		}
	}
	
	@ApiOperation("To check the contactNumber of Company")
	@GetMapping("/checkContactNumber")
	public String checkContactNumber(@RequestParam String contactNumber) {
		log.info("checking company contactNumber exist of not contactNumber is:{}", contactNumber);
		if (clientInformationService.checkContactNumber(contactNumber)) {
			return "Company ContactNumber Already Exists";
		} else {
			return "Company ContactNumber Not Exists";
		}
	}

	@ApiOperation("To check the CompanyWebsite")
	@GetMapping("/checkCompanyWebsite")
	public String checkCompanyWebsite(@RequestParam String companyWebsite) {
		log.info("checking company CompanyWebsite exist of not CompanyWebsite is:{}", companyWebsite);
		if (clientInformationService.checkCompanyWebsite(companyWebsite)) {
			return "CompanyWebsite Already Exists";
		} else {
			return "CompanyWebsite Not Exists";
		}
	}


	@ApiOperation("get suggestiong by name")
	@GetMapping("/client/suggestions")
	public List<ClientDto> getSuggestion(@RequestParam String companyName){
		log.info("getting suggestion by company name,  {}", companyName);
		return clientInformationService.getSuggestionDetails(companyName);
	}

	@ApiOperation("get details by companyname")
	@GetMapping("/getdetailsbycompanyname")
	public List<ClientDto> getDetailsByCompanyName(@RequestParam String companyName)  {
		log.info("get details by company name,{}", companyName);
		return clientInformationService.getDetailsbyCompanyName(companyName);
	}

	@ApiOperation("updating client data by id")
	@PutMapping("/clientupdate")
	public String updateClientDto(@RequestParam int companyId, @RequestBody ClientDto clientDto)
			throws IOException, IllegalAccessException {
		log.info("updating client dto {}", clientDto);
		return clientInformationService.updateClientDto(companyId, clientDto);
	}

}
