package com.xworkz.dream.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.utils.Dropdown;
import com.xworkz.dream.repo.DreamRepo;

@Service
public class UtilService {

	@Autowired
	private DreamRepo repo;

	public Dropdown getDropdown(String spreadsheetId) {

		try {
			List<List<Object>> data = repo.getDropdown(spreadsheetId);
			System.out.println(data.size());
			Dropdown dropdown = new Dropdown();
			List<String> course = dropdown.getCourse();
			List<String> qualifications = dropdown.getQualification();
			List<String> batch = dropdown.getBatch();
			List<String> stream = dropdown.getStream();
			List<String> college = dropdown.getCollege();

			 for (List<Object> list : data) {
			        System.out.println("size" + list);

			        // Check if the list has at least 1 element before accessing it
			        if (list.size() > 0 && !list.get(0).toString().isEmpty()) {
			            course.add((String) list.get(0));
			        }
			        // Check if the list has at least 2 elements before accessing it
			        if (list.size() > 1 && !list.get(1).toString().isEmpty()) {
			            qualifications.add((String) list.get(1));
			        }
			        // Check if the list has at - 3 elements before accessing it
			        if (list.size() > 2 && !list.get(2).toString().isEmpty()) {
			            batch.add((String) list.get(2));
			        }
			        // Check if the list has at least 4 elements before accessing it
			        if (list.size() > 3 && !list.get(3).toString().isEmpty()) {
			            stream.add((String) list.get(3));
			        }
			        // Check if the list has at least 5 elements before accessing it
			        if (list.size() > 4 && !list.get(4).toString().isEmpty()) {
			            college.add((String) list.get(4));
			        }
			    }

			return dropdown;

		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}

}
