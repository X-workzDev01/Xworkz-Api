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

			Dropdown dropdown = new Dropdown();
			List<String> course = dropdown.getCourse();
			List<String> qualifications = dropdown.getQualification();
			List<String> batch = dropdown.getBatch();
			List<String> stream = dropdown.getStream();
			List<String> college = dropdown.getCollege();

			for (List<Object> list : data) {

				if (!list.get(0).toString().isEmpty()) {

					course.add((String) list.get(0));
				}
				if (!list.get(1).toString().isEmpty()) {
					qualifications.add((String) list.get(1));
				}
				if (!list.get(2).toString().isEmpty()) {
					batch.add((String) list.get(2));
				}
				if (!list.get(3).toString().isEmpty()) {
					stream.add((String) list.get(3));
				}
				if (!list.get(4).toString().isEmpty()) {
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
