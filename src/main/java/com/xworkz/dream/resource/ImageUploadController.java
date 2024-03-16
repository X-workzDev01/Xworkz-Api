package com.xworkz.dream.resource;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xworkz.dream.util.Generation;

@RestController
@RequestMapping("/")
public class ImageUploadController {
	@Autowired
	private Generation generate;

	@GetMapping("store")
	public String upload() {
		try {
			generate.googlegetFile();
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
		return "Upload successfully";
	}


}
