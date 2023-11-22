package com.xworkz.dream.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ClientHrData {
	
	
	private List<ClientHrDto> clientHrDetails;
	private int size;

}
