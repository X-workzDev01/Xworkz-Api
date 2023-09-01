package com.xworkz.dream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication

//@EnableAutoConfiguration(exclude = FreeMarkerServletWebConfiguration.class)
public class XworkzApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(XworkzApiApplication.class, args);

	}
}
