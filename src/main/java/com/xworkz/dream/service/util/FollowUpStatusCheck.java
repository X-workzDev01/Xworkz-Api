package com.xworkz.dream.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.xworkz.dream.constants.Status;

import lombok.Data;

@Service
@Data
public class FollowUpStatusCheck {

	private List<String> interested = new ArrayList<String>(Arrays.asList(
			Status.Let_us_know.toString().replace('_', ' '), Status.Need_online.toString().replace('_', ' '),
			Status.Joining.toString(), Status.Interested.toString()));

	private List<String> notInterested = new ArrayList<String>(Arrays.asList(
			Status.Not_interested.toString().replace('_', ' '), Status.Drop_after_free_course.toString().replace('_', ' '),
			Status.Drop_after_placement.toString().replace('_', ' '),
			Status.Higher_Studies.toString().replace('_', ' '),
			Status.Joined_other_institute.toString().replace('_', ' '), Status.Not_joining.toString().replace('_', ' '),
			Status.Wrong_Number.toString().replace('_', ' ')));

	private List<String> rnr = new ArrayList<String>(Arrays.asList(Status.Busy.toString(),
			Status.Incoming_call_not_available.toString().replace('_', ' ').toString(),
			Status.Not_Reachable.toString().replace('_', ' '), Status.RNR.toString(),
			Status.Call_Drop.toString().replace('_', ' ')));

}
