package com.xworkz.dream.service.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.dream.constants.ServiceConstant;
import com.xworkz.dream.constants.Status;
import com.xworkz.dream.dto.FollowUpDto;
import com.xworkz.dream.dto.TraineeDto;
import com.xworkz.dream.dto.utils.StatusList;
import com.xworkz.dream.wrapper.DreamWrapper;

@Service
public class FollowUpUtil {
	@Autowired
	private DreamWrapper wrapper;
	@Autowired
	private FollowUpStatusCheck FollowUpStatusCheck;

	public TraineeDto getTraineeDtoByEmail(List<List<Object>> traineeData, String email) {
		if (traineeData == null || email == null) {
			return null;
		}
		return traineeData.stream()
				.filter(row -> row.size() > 2 && row.get(2) != null && row.get(2).toString().equalsIgnoreCase(email))
				.map(wrapper::listToDto).findFirst().orElse(null);
	}

	public List<FollowUpDto> getFollowupList(List<List<Object>> followUpList, List<List<Object>> traineeData) {
		List<FollowUpDto> filteredFollowUp = new ArrayList<FollowUpDto>();
		if (filteredFollowUp != null) {
			followUpList.stream().map(wrapper::listToFollowUpDTO)
					.filter(dto -> dto.getFlagSheet().equalsIgnoreCase(ServiceConstant.ACTIVE.toString()))
					.forEach(followupDto -> {

						TraineeDto traineeDto = getTraineeDtoByEmail(traineeData,
								followupDto.getBasicInfo().getEmail());
						followupDto.setCourseName(traineeDto.getCourseInfo().getCourse());
						followupDto.setYear(traineeDto.getEducationInfo().getYearOfPassout());
						followupDto.setCollegeName(traineeDto.getEducationInfo().getCollegeName());
						filteredFollowUp.add(followupDto);
					});
		}
		return filteredFollowUp;
	}

	public Predicate<FollowUpDto> byStatusAndDateAndCollegeName(String status, String courseName, String date,
			String collegeName, DateTimeFormatter dateFormatter, StatusList statusList,
			Predicate<FollowUpDto> predicate) {
		if (!status.equals("null") && courseName.equals("null") && !date.equals("null")
				&& !collegeName.equals("null")) {
			if (status.equalsIgnoreCase(Status.Interested.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getInterested().contains(
						followUpData.getCurrentStatus()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.Not_interested.toString().replace('_', ' '))) {
				predicate = followUpData -> FollowUpStatusCheck.getNotInterested().contains(
						followUpData.getCurrentStatus()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.RNR.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getRnr().contains(followUpData.getCurrentStatus())
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.Never_followUp.toString().replace('_', ' '))) {
				predicate = followUpData -> followUpData.getCurrentlyFollowedBy().equalsIgnoreCase(
						Status.NONE.toString()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.Past_followup.toString().replace('_', ' '))) {
				predicate = followUpData -> statusList.getStatusCheck().contains(followUpData.getCurrentStatus())
						&& followUpData.getAdminDto().getUpdatedBy() != null
						&& !followUpData.getAdminDto().getUpdatedBy().equalsIgnoreCase("NA")
						&& (LocalDate.parse(LocalDate.parse(followUpData.getCallback()).format(dateFormatter)))
								.isBefore(LocalDate.now().minusDays(2))
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else {
				predicate = followUpData -> followUpData.getCurrentStatus().equalsIgnoreCase(status)
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			}
		}
		return predicate;
	}

	public Predicate<FollowUpDto> byStatusAndCourseName(String status, String courseName, String date,
			String collegeName, DateTimeFormatter dateFormatter, StatusList statusList,
			Predicate<FollowUpDto> predicate) {
		if (!status.equals("null") && !courseName.equals("null") && date.equals("null") && collegeName.equals("null")) {
			if (status.equalsIgnoreCase(Status.Interested.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getInterested().contains(
						followUpData.getCurrentStatus()) && followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.Not_interested.toString().replace('_', ' '))) {
				predicate = followUpData -> FollowUpStatusCheck.getNotInterested().contains(
						followUpData.getCurrentStatus()) && followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.RNR.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getRnr().contains(followUpData.getCurrentStatus())
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.Never_followUp.toString().replace('_', ' '))) {
				predicate = followUpData -> followUpData.getCurrentlyFollowedBy().equalsIgnoreCase(
						Status.NONE.toString()) && followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.Past_followup.toString().replace('_', ' '))) {
				predicate = followUpData -> statusList.getStatusCheck().contains(followUpData.getCurrentStatus())
						&& followUpData.getAdminDto().getUpdatedBy() != null
						&& !followUpData.getAdminDto().getUpdatedBy().equalsIgnoreCase("NA")
						&& (LocalDate.parse(LocalDate.parse(followUpData.getCallback()).format(dateFormatter)))
								.isBefore(LocalDate.now().minusDays(2))
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else {
				predicate = followUpData -> followUpData.getCurrentStatus().equalsIgnoreCase(status)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			}

		}
		return predicate;
	}

	public Predicate<FollowUpDto> byStatusCourseNameAndDateAndCollegeName(String status, String courseName, String date,
			String collegeName, DateTimeFormatter dateFormatter, StatusList statusList,
			Predicate<FollowUpDto> predicate) {
		if (!status.equals("null") && !courseName.equals("null") && !date.equals("null")
				&& !collegeName.equals("null")) {
			if (status.equalsIgnoreCase(Status.Interested.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getInterested().contains(
						followUpData.getCurrentStatus()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.Not_interested.toString().replace('_', ' '))) {
				predicate = followUpData -> FollowUpStatusCheck.getNotInterested().contains(
						followUpData.getCurrentStatus()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.RNR.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getRnr().contains(followUpData.getCurrentStatus())
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.Never_followUp.toString().replace('_', ' '))) {
				predicate = followUpData -> followUpData.getCurrentlyFollowedBy().equalsIgnoreCase(
						Status.NONE.toString()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.Past_followup.toString().replace('_', ' '))) {
				predicate = followUpData -> statusList.getStatusCheck().contains(followUpData.getCurrentStatus())
						&& followUpData.getAdminDto().getUpdatedBy() != null
						&& !followUpData.getAdminDto().getUpdatedBy().equalsIgnoreCase("NA")
						&& (LocalDate.parse(LocalDate.parse(followUpData.getCallback()).format(dateFormatter)))
								.isBefore(LocalDate.now().minusDays(2))
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else {
				predicate = followUpData -> followUpData.getCurrentStatus().equalsIgnoreCase(status)
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			}
		}
		return predicate;
	}

	public Predicate<FollowUpDto> byStatusAndCourseNameAndCollegeName(String status, String courseName, String date,
			String collegeName, DateTimeFormatter dateFormatter, StatusList statusList,
			Predicate<FollowUpDto> predicate) {
		if (!status.equals("null") && !courseName.equals("null") && date.equals("null")
				&& !collegeName.equals("null")) {
			if (status.equalsIgnoreCase(Status.Interested.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getInterested().contains(
						followUpData.getCurrentStatus()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.Not_interested.toString().replace('_', ' '))) {
				predicate = followUpData -> FollowUpStatusCheck.getNotInterested().contains(
						followUpData.getCurrentStatus()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.RNR.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getRnr().contains(followUpData.getCurrentStatus())
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.Never_followUp.toString().replace('_', ' '))) {
				predicate = followUpData -> followUpData.getCurrentlyFollowedBy().equalsIgnoreCase(
						Status.NONE.toString()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.Past_followup.toString().replace('_', ' '))) {
				predicate = followUpData -> statusList.getStatusCheck().contains(followUpData.getCurrentStatus())
						&& followUpData.getAdminDto().getUpdatedBy() != null
						&& !followUpData.getAdminDto().getUpdatedBy().equalsIgnoreCase("NA")
						&& (LocalDate.parse(LocalDate.parse(followUpData.getCallback()).format(dateFormatter)))
								.isBefore(LocalDate.now().minusDays(2))
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else {
				predicate = followUpData -> followUpData.getCurrentStatus().equalsIgnoreCase(status)
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			}
		}
		return predicate;
	}

	public Predicate<FollowUpDto> byStatusAndCollegeName(String status, String courseName, String date,
			String collegeName, DateTimeFormatter dateFormatter, StatusList statusList,
			Predicate<FollowUpDto> predicate) {
		if (!status.equals("null") && courseName.equals("null") && date.equals("null") && !collegeName.equals("null")) {
			if (status.equalsIgnoreCase(Status.Interested.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getInterested().contains(
						followUpData.getCurrentStatus()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName);
			} else if (status.equalsIgnoreCase(Status.Not_interested.toString().replace('_', ' '))) {
				predicate = followUpData -> FollowUpStatusCheck.getNotInterested().contains(
						followUpData.getCurrentStatus()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName);
			} else if (status.equalsIgnoreCase(Status.RNR.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getRnr().contains(followUpData.getCurrentStatus())
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName);
			} else if (status.equalsIgnoreCase(Status.Never_followUp.toString().replace('_', ' '))) {
				predicate = followUpData -> followUpData.getCurrentlyFollowedBy().equalsIgnoreCase(
						Status.NONE.toString()) && followUpData.getCollegeName().equalsIgnoreCase(collegeName);
			} else if (status.equalsIgnoreCase(Status.Past_followup.toString().replace('_', ' '))) {
				predicate = followUpData -> statusList.getStatusCheck().contains(followUpData.getCurrentStatus())
						&& followUpData.getAdminDto().getUpdatedBy() != null
						&& !followUpData.getAdminDto().getUpdatedBy().equalsIgnoreCase("NA")
						&& (LocalDate.parse(LocalDate.parse(followUpData.getCallback()).format(dateFormatter)))
								.isBefore(LocalDate.now().minusDays(2))
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName);
			} else {
				predicate = followUpData -> followUpData.getCurrentStatus().equalsIgnoreCase(status)
						&& followUpData.getCollegeName().equalsIgnoreCase(collegeName);
			}
		}
		return predicate;
	}

	public Predicate<FollowUpDto> byStatus(String status, String courseName, String date, String collegeName,
			DateTimeFormatter dateFormatter, StatusList statusList, Predicate<FollowUpDto> predicate) {
		if (!status.equals("null") && courseName.equals("null") && date.equals("null") && collegeName.equals("null")) {
			if (status.equalsIgnoreCase(Status.Interested.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getInterested()
						.contains(followUpData.getCurrentStatus());
			} else if (status.equalsIgnoreCase(Status.Not_interested.toString().replace('_', ' '))) {
				predicate = followUpData -> FollowUpStatusCheck.getNotInterested()
						.contains(followUpData.getCurrentStatus());
			} else if (status.equalsIgnoreCase(Status.RNR.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getRnr().contains(followUpData.getCurrentStatus());
			} else if (status.equalsIgnoreCase(Status.Never_followUp.toString().replace('_', ' '))) {
				predicate = followUpData -> followUpData.getCurrentlyFollowedBy()
						.equalsIgnoreCase(Status.NONE.toString());
			} else if (status.equalsIgnoreCase(Status.Past_followup.toString().replace('_', ' '))) {
				predicate = followUpData -> statusList.getStatusCheck().contains(followUpData.getCurrentStatus())
						&& followUpData.getAdminDto().getUpdatedBy() != null
						&& !followUpData.getAdminDto().getUpdatedBy().equalsIgnoreCase("NA")
						&& (LocalDate.parse(LocalDate.parse(followUpData.getCallback()).format(dateFormatter)))
								.isBefore(LocalDate.now().minusDays(2));
			} else {
				predicate = followUpData -> followUpData.getCurrentStatus().equalsIgnoreCase(status);
			}
		}
		return predicate;
	}

	public Predicate<FollowUpDto> byStatusAndCourseAndDate(String status, String courseName, String date,
			String collegeName, DateTimeFormatter dateFormatter, StatusList statusList,
			Predicate<FollowUpDto> predicate) {
		if (!status.equals("null") && !courseName.equals("null") && !date.equals("null")
				&& collegeName.equals("null")) {
			if (status.equalsIgnoreCase(Status.Interested.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getInterested()
						.contains(followUpData.getCurrentStatus()) && followUpData.getCallback().equalsIgnoreCase(date)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.Not_interested.toString().replace('_', ' '))) {
				predicate = followUpData -> FollowUpStatusCheck.getNotInterested()
						.contains(followUpData.getCurrentStatus()) && followUpData.getCallback().equalsIgnoreCase(date)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.RNR.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getRnr().contains(followUpData.getCurrentStatus())
						&& followUpData.getCallback().equalsIgnoreCase(date)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.Never_followUp.toString().replace('_', ' '))) {
				predicate = followUpData -> followUpData.getCurrentlyFollowedBy()
						.equalsIgnoreCase(Status.NONE.toString()) && followUpData.getCallback().equalsIgnoreCase(date)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else if (status.equalsIgnoreCase(Status.Past_followup.toString().replace('_', ' '))) {
				predicate = followUpData -> statusList.getStatusCheck().contains(followUpData.getCurrentStatus())
						&& followUpData.getAdminDto().getUpdatedBy() != null
						&& !followUpData.getAdminDto().getUpdatedBy().equalsIgnoreCase("NA")
						&& (LocalDate.parse(LocalDate.parse(followUpData.getCallback()).format(dateFormatter)))
								.isBefore(LocalDate.now().minusDays(2))
						&& followUpData.getCallback().equalsIgnoreCase(date)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			} else {
				predicate = followUpData -> followUpData.getCurrentStatus().equalsIgnoreCase(status)
						&& followUpData.getCallback().equalsIgnoreCase(date)
						&& followUpData.getCourseName().equalsIgnoreCase(courseName);
			}
		}
		return predicate;
	}

	public Predicate<FollowUpDto> byStatusAndDate(String status, String courseName, String date, String collegeName,
			DateTimeFormatter dateFormatter, StatusList statusList, Predicate<FollowUpDto> predicate) {
		if (!status.equals("null") && courseName.equals("null") && !date.equals("null") && collegeName.equals("null")) {
			if (status.equalsIgnoreCase(Status.Interested.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getInterested()
						.contains(followUpData.getCurrentStatus()) && followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.Not_interested.toString().replace('_', ' '))) {
				predicate = followUpData -> FollowUpStatusCheck.getNotInterested()
						.contains(followUpData.getCurrentStatus()) && followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.RNR.toString())) {
				predicate = followUpData -> FollowUpStatusCheck.getRnr().contains(followUpData.getCurrentStatus())
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.Never_followUp.toString().replace('_', ' '))) {
				predicate = followUpData -> followUpData.getCurrentlyFollowedBy()
						.equalsIgnoreCase(Status.NONE.toString()) && followUpData.getCallback().equalsIgnoreCase(date);
			} else if (status.equalsIgnoreCase(Status.Past_followup.toString().replace('_', ' '))) {
				predicate = followUpData -> statusList.getStatusCheck().contains(followUpData.getCurrentStatus())
						&& followUpData.getAdminDto().getUpdatedBy() != null
						&& !followUpData.getAdminDto().getUpdatedBy().equalsIgnoreCase("NA")
						&& (LocalDate.parse(LocalDate.parse(followUpData.getCallback()).format(dateFormatter)))
								.isBefore(LocalDate.now().minusDays(2))
						&& followUpData.getCallback().equalsIgnoreCase(date);
			} else {
				predicate = followUpData -> followUpData.getCurrentStatus().equalsIgnoreCase(status)
						&& followUpData.getCallback().equalsIgnoreCase(date);
			}
		}
		return predicate;
	}

}
