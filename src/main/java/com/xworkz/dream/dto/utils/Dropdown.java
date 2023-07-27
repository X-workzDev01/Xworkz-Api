package com.xworkz.dream.dto.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Dropdown {

	private List<String> course;
	private List<String> qualification;
	private List<String> batch;
	private List<String> stream;
	private List<String> college;
	private List<String> yearofpass;
	private List<String> offered;
	private List<String> branchname;

	public Dropdown() {
		super();
		this.course = new ArrayList<String>();
		this.qualification = new ArrayList<String>();
		this.batch = new ArrayList<String>();
		this.stream = new ArrayList<String>();
		this.college = new ArrayList<String>();
		this.yearofpass = new ArrayList<String>();
		this.offered = new ArrayList<String>();
		this.branchname = new ArrayList<String>();

	}

}
