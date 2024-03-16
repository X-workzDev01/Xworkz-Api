package com.xworkz.dream.constants;

import lombok.Getter;

@Getter
public enum ClientHrConstant {

	COLUMN_ID(0), COLUMN_COMPANY_ID(1), COLUMN_SPOCNAME(2), COLUMN_HR_EMAIL(3), COLUMN_CONTACT_NUMBER(4),
	COLUMN_DESIGNATION(5), COLUMN_COMMENTS(6), COLUMN_CREATED_BY(7), COLUMN_CREATED_ON(8), COLUMN_UPDATED_BY(9),
	COLUMN_UPDATED_ON(10);

	private int index;

	private ClientHrConstant(int index) {
		this.index = index;
	}
}
