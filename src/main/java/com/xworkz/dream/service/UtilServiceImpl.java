package com.xworkz.dream.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.dto.utils.ClientDropDown;
import com.xworkz.dream.dto.utils.Dropdown;
import com.xworkz.dream.repository.DreamRepository;

@Service
public class UtilServiceImpl implements UtilService {

	@Autowired
	private DreamRepository repo;

	private static final Logger log = LoggerFactory.getLogger(UtilServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public Dropdown getDropdown(String spreadsheetId) {
		try {
			List<List<Object>> data = repo.getDropdown(spreadsheetId);
			if (data == null) {
				log.warn("Dropdown data is null for spreadsheetId: {}", spreadsheetId);
				return null;
			}

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
			checkAndAssignValues(data, course, qualifications, batch, stream, college, yearofpass, offered, branchname,
					status);
			sortingDropDownData(course, qualifications, batch, stream, college, yearofpass, offered, branchname,
					status);
			log.info("Dropdown data fetched successfully for spreadsheetId: {}", spreadsheetId);
			return dropdown;

		} catch (IOException e) {
			log.error("Error loading dropdowns for spreadsheetId: {}", spreadsheetId, e);
		}
		return null;
	}

	private void checkAndAssignValues(List<List<Object>> data, List<String> course, List<String> qualifications,
			List<String> batch, List<String> stream, List<String> college, List<String> yearofpass,
			List<String> offered, List<String> branchname, List<String> status) {
		log.info("Checking and assigning values to dropdown lists");
		for (List<Object> list : data) {
			if (list != null) {
				if (list.size() > 0 && !list.get(0).toString().isEmpty()) {
					course.add((String) list.get(0));
				}
				if (list.size() > 1 && !list.get(1).toString().isEmpty()) {
					qualifications.add((String) list.get(1));
				}
				if (list.size() > 2 && !list.get(2).toString().isEmpty()) {
					batch.add((String) list.get(2));
				}
				if (list.size() > 3 && !list.get(3).toString().isEmpty()) {
					stream.add((String) list.get(3));
				}
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
		}
	}

	@SuppressWarnings("unchecked")
	private void sortingDropDownData(List<String>... lists) {
		Arrays.stream(lists).forEach(list -> {
			Collections.sort(list);
			list.add("Others");
		});
		log.info("Dropdown data sorted successfully");

	}

	@Override
	public ClientDropDown getClientDropDown() {
		List<List<Object>> listOfClientDropDown = repo.getClientDropDown();
		if (listOfClientDropDown == null) {
			return null;
		}
		ClientDropDown dropdown = new ClientDropDown();
		List<String> clientType = dropdown.getClientType();
		List<String> callingStatus = dropdown.getCallingStatus();
		List<String> hrDesignation = dropdown.getHrDesignation();
		List<String> sourceOfConnection = dropdown.getSourceOfConnection();
		List<String> sourceOfLocation = dropdown.getSourceOfLocation();

		for (List<Object> list : listOfClientDropDown) {
			if (list != null) {
				if (list.size() > 0 && !list.get(0).toString().isEmpty()) {
					clientType.add(list.get(0).toString());
				}
				if (list.size() > 1 && !list.get(1).toString().isEmpty()) {
					sourceOfConnection.add(list.get(1).toString());
				}
				if (list.size() > 2 && !list.get(2).toString().isEmpty()) {
					sourceOfLocation.add(list.get(2).toString());
				}
				if (list.size() > 3 && !list.get(3).toString().isEmpty()) {
					hrDesignation.add(list.get(3).toString());
				}
				if (list.size() > 4 && !list.get(4).toString().isEmpty()) {
					callingStatus.add(list.get(4).toString());
				}
			}
		}
		sortingDropDownData(clientType, callingStatus, hrDesignation, sourceOfConnection, sourceOfLocation);
		return dropdown;
	}

}
