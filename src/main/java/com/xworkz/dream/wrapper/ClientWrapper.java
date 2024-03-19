package com.xworkz.dream.wrapper;

import java.util.List;

import com.xworkz.dream.clientDtos.ClientValueDto;
import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.HrFollowUpDto;

public interface ClientWrapper {

	 HrFollowUpDto listToHrFollowUpDto(List<Object> row);
	 ClientDto listToClientDto(List<Object> row);
	 ClientHrDto listToClientHrDto(List<Object> row);
	 ClientValueDto listToClientValueDto(List<Object> row);

}
