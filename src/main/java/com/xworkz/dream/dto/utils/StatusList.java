package com.xworkz.dream.dto.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.xworkz.dream.constants.Status;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StatusList {
	private List<String> statusCheck = Stream.of(Status.Busy.toString(), Status.,Status.New.toString(),
			Status.Interested.toString(), Status.RNR.toString(), Status.Not_interested.toString().replace('_', ' '),
			Status.Incomingcall_not_available.toString().replace('_', ' '),
			Status.Not_reachable.toString().replace('_', ' '), Status.Let_us_know.toString().replace('_', ' '),
			Status.Need_online.toString().replace('_', ' ')).collect(Collectors.toList());

}
