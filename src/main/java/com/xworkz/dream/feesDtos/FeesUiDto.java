package com.xworkz.dream.feesDtos;

import com.xworkz.dream.dto.AuditDto;

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
	private AuditDto adminDto;
	private String status;

}
