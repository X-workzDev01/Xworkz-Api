package com.xworkz.dream.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "emailableClinet", url = "https://api.emailable.com/v1")
public interface EmailableClient {

	
	@GetMapping("/verify")
	String verifyEmail(@RequestParam("email") String email, @RequestParam("api_key") String apiKey);

}
