package com.xworkz.dream.resource;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.service.DreamService;

import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/api")
public class DreamApiController {
	
	@Autowired
	private DreamService service;
	
	
	
	@ApiOperation(value = "To register the trainee details in the google sheets")
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestParam String spreadsheetId, @RequestBody TraineeDto values)
			throws IOException {
		System.out.println(values);
		service.writeData(spreadsheetId, values);
		
		return ResponseEntity.ok("Registered Successfully");
	}
			
	@ApiOperation(value = "To register Check whether email already exist while registering")		
	@GetMapping("/emailCheck")
	public ResponseEntity<String> emailCheck(@RequestParam String spreadsheetId , @RequestParam String email){
		return service.emailCheck(spreadsheetId, email);
	}
	
	@ApiOperation(value = "To register Check whether contact number already exist while registering")	
	@GetMapping("/contactNumberCheck")
	public ResponseEntity<String> contactNumberCheck(@RequestParam String spreadsheetId , @RequestParam Long contactNumber){
		return service.contactNumberCheck(spreadsheetId, contactNumber);
	}
	
	
	

	

}
