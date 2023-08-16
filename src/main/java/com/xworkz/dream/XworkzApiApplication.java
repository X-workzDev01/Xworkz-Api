package com.xworkz.dream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = FreeMarkerServletWebConfiguration.class)
public class XworkzApiApplication {

	public static void main(String[] args) {
		
	    SpringApplication.run(XworkzApiApplication.class, args);
	    
}
}
