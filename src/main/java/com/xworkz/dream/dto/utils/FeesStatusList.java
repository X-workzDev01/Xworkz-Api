package com.xworkz.dream.dto.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.xworkz.dream.constants.Status;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
@Data
public class FeesStatusList {

	private List<String> statusList = Stream.of(Status.FEES_DUE.toString().replace('_', ' '), Status.PENDING.toString())
			.collect(Collectors.toList());

}
