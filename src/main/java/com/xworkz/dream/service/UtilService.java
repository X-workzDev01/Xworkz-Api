package com.xworkz.dream.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.utils.Dropdown;
import com.xworkz.dream.repository.DreamRepositoryImpl;

@Service
public class UtilService {

	@Autowired
	private DreamRepositoryImpl repo;

	public Dropdown getDropdown(String spreadsheetId) {

		try {
			List<List<Object>> data = repo.getDropdown(spreadsheetId);
			// System.out.println(data.size());
			Dropdown dropdown = new Dropdown();
			List<String> course = dropdown.getCourse();

			List<String> qualifications = dropdown.getQualification();
			List<String> batch = dropdown.getBatch();
			List<String> stream = dropdown.getStream();
			List<String> college = dropdown.getCollege();
			List<String> yearofpass = dropdown.getYearofpass();
			List<String> offered = dropdown.getOffered();
			List<String> branchname = dropdown.getBranchname();
			List<String> status = dropdown.getStatus();
			for (List<Object> list : data) {
				// System.out.println("size" + list);

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
				if (list.size() > 5 && !list.get(5).toString().isEmpty()) {
					yearofpass.add((String) list.get(5));
				}
				if (list.size() > 6 && !list.get(6).toString().isEmpty()) {
					offered.add((String) list.get(6));
				}
				if (list.size() > 7 && !list.get(7).toString().isEmpty()) {
					branchname.add((String) list.get(7));
				}
				if (list.size() > 8 && !list.get(8).toString().isEmpty()) {
					status.add((String) list.get(8));
				}
			}

			// sorting
			Collections.sort(course);
			course.add("Others");
			Collections.sort(qualifications);
			qualifications.add("Others");
			Collections.sort(batch);
			batch.add("Others");
			Collections.sort(stream);
			stream.add("Others");
			Collections.sort(college);
			college.add("Others");
			Collections.sort(yearofpass);
			yearofpass.add("Others");
			Collections.sort(offered);
			offered.add("Others");
			Collections.sort(branchname);
			branchname.add("Others");
			Collections.sort(status);
			status.add("Others");

			return dropdown;

		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}

}
