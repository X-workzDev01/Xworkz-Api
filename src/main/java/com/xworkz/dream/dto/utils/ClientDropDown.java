package com.xworkz.dream.dto.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ClientDropDown {

	private List<String> clientType;
	private List<String> sourceOfConnection;
	private List<String> sourceOfLocation;
	private List<String> hrDesignation;
	private List<String> callingStatus;

	public ClientDropDown() {
	this.clientType = new ArrayList<String>();
	this.sourceOfConnection = new ArrayList<String>();
	this.sourceOfLocation = new ArrayList<String>();
	this.callingStatus = new ArrayList<String>();
	this.hrDesignation = new ArrayList<String>();
	}
}
