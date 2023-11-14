package com.xworkz.dream.wrapper;

import java.util.List;

import com.xworkz.dream.dto.ClientDto;

public interface ClientWrapper {

	abstract void setValuesToClientDto(ClientDto dto);

	abstract ClientDto listToClientDto(List<Object> row);

}
