package com.xworkz.dream.resource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.HrFollowUpDto;
import com.xworkz.dream.service.HrFollowUpService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class HrFollowUpController {

	@Autowired
	private HrFollowUpService hrFollowUpService;

	private static final Logger log = LoggerFactory.getLogger(HrFollowUpController.class);

	@ApiOperation("To save Hr follow up data ")
	@PostMapping("/hrfollowup")
	public String saveHrFollowUpDetails(@RequestBody HrFollowUpDto dto) {
		log.info("Saving hr follow up details {} ", dto);
		System.err.println(dto);
		return hrFollowUpService.saveHrFollowUpDetails(dto);
	}

	@ApiOperation("Read the Hr Follow up details")
	@GetMapping("/gethrfollowupdetails")
	public List<HrFollowUpDto> getHrFollowUpDetails(@RequestParam int hrId) {
		log.info("Read Hr follow up details,{}", hrId);
		return hrFollowUpService.getHrFollowUpDetailsBy(hrId);
	}

	@ApiOperation("To get the Follow up details by Company Id")
	@GetMapping("/getFollowUpDetailsById")
	public List<HrFollowUpDto> getFollowUpDetails(@RequestParam Integer companyId) {
		log.info("Read follow up details by company id {},", companyId);
		return hrFollowUpService.getFollowUpDetails(companyId);
	}

}
