package com.xworkz.dream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableCaching
@EnableFeignClients("com.xworkz.dream.interfaces")

public class XworkzApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(XworkzApiApplication.class, args);	
	} 
}
