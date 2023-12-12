package com.xworkz.dream.wrapper;

import java.util.List;

import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.HrFollowUpDto;

public interface ClientWrapper {

	abstract HrFollowUpDto listToHrFollowUpDto(List<Object> row);
	abstract ClientDto listToClientDto(List<Object> row);
	abstract ClientHrDto listToClientHrDto(List<Object> row);

}
