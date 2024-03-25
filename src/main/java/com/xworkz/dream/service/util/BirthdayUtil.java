package com.xworkz.dream.service.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.xworkz.dream.dto.BirthdayDetailsDto;

import lombok.Data;

@Data
@Component
public class BirthdayUtil {

	public Predicate<BirthdayDetailsDto> predicateBySelected(String date, String courseName, String month) {
		Predicate<BirthdayDetailsDto> predicate = null;

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");
		DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");

		if (!courseName.equals("null") && date.equals("null")) {
			predicate = dto -> dto.getCourseName().equalsIgnoreCase(courseName);
		}

		if (courseName.equals("null") && !date.equals("null")) {
			predicate = dto -> !dto.getBasicInfoDto().getDateOfBirth().equals("NA")
					&& LocalDate.parse(dto.getBasicInfoDto().getDateOfBirth()).format(dateFormatter).toString()
							.equals(LocalDate.parse(date).format(dateFormatter).toString());
		}
		if (!courseName.equals("null") && !date.equals("null")) {
			predicate = dto -> !dto.getBasicInfoDto().getDateOfBirth().equals("NA")
					&& LocalDate.parse(dto.getBasicInfoDto().getDateOfBirth()).format(dateFormatter).toString()
							.equals(LocalDate.parse(date).format(dateFormatter).toString())
					&& dto.getCourseName().equalsIgnoreCase(courseName);
		}

		if (!month.equals("null") && courseName.equals("null") && date.equals("null")) {
			predicate = dto -> !dto.getBasicInfoDto().getDateOfBirth().equals("NA")
					&& LocalDate.parse(dto.getBasicInfoDto().getDateOfBirth()).format(monthFormatter).toString()
							.equals(LocalDate.parse(month.concat("-01")).format(monthFormatter).toString());
		}

		if (!month.equals("null") && !courseName.equals("null") && date.equals("null")) {
			predicate = dto -> !dto.getBasicInfoDto().getDateOfBirth().equals("NA")
					&& LocalDate.parse(dto.getBasicInfoDto().getDateOfBirth()).format(monthFormatter).toString()
							.equals(LocalDate.parse(month.concat("-01")).format(monthFormatter).toString())
					&& dto.getCourseName().equalsIgnoreCase(courseName);
		}
		return predicate;
	}

}
