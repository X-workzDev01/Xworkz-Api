package com.xworkz.dream.feesDtos;

import com.xworkz.dream.dto.AdminDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Data
public class FeesUiDto {

	private String email;
	private String name;
	private AdminDto adminDto;
	private String status;
	private String transectionId;

}
