package com.xworkz.dream.dto.utils;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Team {
	@JsonProperty("email")
	private String email;
	@JsonProperty("name")
	private String name;

	public Team(String email, String name) {
		this.email = email;
	}

}
