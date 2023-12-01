package com.xworkz.dream.wrapper;

import java.util.List;

import com.xworkz.dream.dto.ClientHrDto;

public interface ClientHrWrapper {

	abstract void setValuesToClientHrDto(ClientHrDto dto);

	abstract ClientHrDto listToClientHrDto(List<Object> row);

}
