package com.xworkz.dream.constants;

import lombok.Getter;

@Getter
public enum ClientConstant {
	COLUMN_ID(0), COLUMN_COMPANY_NAME(1), COLUMN_EMAIL(2), COLUMN_LANDLINE_NUMBER(3), COLUMN_WEBSITE(4),
	COLUMN_LOCATION(5), COLUMN_FOUNDER(6), COLUMN_SOURCE_OF_CONNECTION(7), COLUMN_COMPANY_TYPE(8), COLUMN_ADDRESS(9),
	COLUMN_STATUS(10), COLUMN_CREATED_BY(11), COLUMN_CREATED_ON(12), COLUMN_UPDATED_BY(13), COLUMN_UPDATED_ON(14);

	private int index;

	private ClientConstant(int index) {
		this.index = index;
	}

}
