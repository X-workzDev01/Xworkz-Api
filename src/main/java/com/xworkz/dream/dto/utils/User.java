package com.xworkz.dream.dto.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {

	@JsonProperty("email")
	private String email;
	@JsonProperty("name")
	private String name;
	private int otp;
	private LocalDateTime otpExpiration;
	private String loginTime;

	public User(String email, String name) {
		this.name = name;
		this.email = email;
	}

	

}
