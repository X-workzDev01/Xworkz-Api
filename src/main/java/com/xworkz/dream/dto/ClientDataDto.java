package com.xworkz.dream.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientDataDto {

	private List<ClientDto> clientDtos;
	private int size;
}
