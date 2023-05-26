package com.xworkz.dream.resource;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DreamApiController {
	
	
	@GetMapping("/test")
	public String test() {
		return "HELLO";
	}
	

}
