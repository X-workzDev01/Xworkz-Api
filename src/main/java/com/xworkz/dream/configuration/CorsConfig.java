package com.xworkz.dream.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	public void addCorsMappings(CorsRegistry register) {
		register.addMapping("/**")
				.allowedOrigins("https://ombn.in/**", "https://www.x-workz.in/**","https://www.x-workz.in/**", "http://localhost:3000",
						"https://ombn.in/x-workz/login", "https://ombn.in/x-workz", "https://ombn.in")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD");
	}

}