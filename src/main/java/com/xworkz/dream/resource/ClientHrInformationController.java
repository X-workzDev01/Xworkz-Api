package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	@PostMapping("/registerclienthr")
	public String saveClientHrInformation(@RequestBody ClientHrDto clientHrDto)
			throws IllegalAccessException, IOException {
		log.info("client Hr controller {}", clientHrDto);
		return clientHrService.saveClientHrInformation(clientHrDto);
	}

	@ApiOperation("To save client HR information")
	@GetMapping("/hrdetails")
	public ClientHrData readData(@RequestParam int startingIndex, @RequestParam int maxRows,@RequestParam int companyId){
		log.debug("Reading client HR information");
		return clientHrService.readData(startingIndex, maxRows,companyId);
	}

	@ApiOperation("To check Whether CompanyName is exists or not")
	@GetMapping("/hremailcheck")
	public String checkHrEmail(@RequestParam String hrEmail) {
		log.info("checking company is already exist or not  {}", hrEmail);
		if (clientHrService.hrEmailcheck(hrEmail)) {
			return "Email already exists.";
		} else {
			return "Email does not exist.";
		}
	}
	
	@ApiOperation("To check Whether CompanyName is exists or not")
	@GetMapping("/hrcontactnumbercheck")
	public String checkHrContactNumberCheck(@RequestParam Long contactNumber) throws IOException {
		log.info("checking company is already exist or not  {}", contactNumber);
		if (clientHrService.hrContactNumberCheck(contactNumber)) {
			return "Contact Number Already exist.";
		} else {
			return "Contact Number does not exist.";
		}
	}

	
	@ApiOperation("To get the HR name based on the companyID")
	@GetMapping("/gethrdetails")
	public List<ClientHrDto> getHrNameByCompanyId(@RequestParam int companyId) throws IOException{
		log.info("get HR name by companyId, {}",companyId);
		return clientHrService.getHrDetailsByCompanyId(companyId);
	}
	
	@ApiOperation("To get the HR details by HR ID")
	@GetMapping("/getdetailsbyhrid")
	public ClientHrDto getHRDetailsByHrId(@RequestParam int hrId) throws IOException {
		log.info("get details by Hr Id: {}",hrId);
		return clientHrService.getHRDetailsByHrId(hrId);
	}
	
	@ApiOperation("Update the HR details by Id")
	@PutMapping("/updatebyId")
	public String updateHrDetails(@RequestParam int hrId,@RequestBody ClientHrDto clientHrDto) throws IllegalAccessException, IOException {
		log.info("updating Hr details by id, {}",hrId);
		return clientHrService.updateHrDetails(hrId,clientHrDto);
	}
	
}
