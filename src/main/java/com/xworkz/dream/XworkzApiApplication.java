package com.xworkz.dream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.xworkz.dream.interfaces")

public class XworkzApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(XworkzApiApplication.class, args);

	} 
}





 