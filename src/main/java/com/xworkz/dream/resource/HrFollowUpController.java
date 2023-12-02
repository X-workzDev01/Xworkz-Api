package com.xworkz.dream.resource;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.HrFollowUpDto;
import com.xworkz.dream.service.HrFollowUpService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api")
public class HrFollowUpController {
	
	@Autowired
	private HrFollowUpService hrFollowUpService;
	
	private static final Logger log = LoggerFactory.getLogger(HrFollowUpController.class);
	
	@ApiOperation("To save Hr follow up data ")
	@PostMapping("/hrfollowup")
    public String saveHrFollowUpDetails(@RequestBody HrFollowUpDto  dto) throws IllegalAccessException, IOException {
		log.info("Saving hr follow up details {} ",dto);
		return 	hrFollowUpService.saveHrFollowUpDetails(dto);
	}
	
	@ApiOperation("Read the Hr Follow up details")
	@GetMapping("/gethrfollowupdetails")
	public List<HrFollowUpDto> getHrFollowUpDetails(@RequestParam int hrId) throws IOException{
		log.debug("Read Hr follow up details");
		return hrFollowUpService.getHrFollowUpDetailsBy(hrId);
	}
	
}
