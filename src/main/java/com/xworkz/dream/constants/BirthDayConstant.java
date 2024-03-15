package com.xworkz.dream.constants;

import lombok.Getter;

@Getter
public enum BirthDayConstant {
	
	COLUMN_ID(0),COLUMN_TRAINEE_EMAIL(1),COLUMN_BIRTHDAY_MAILSET(2),COLUMN_CREATED_BY(3),COLUMN_CREATED_ON(4)
	,COLUMN_UPDATED_BY(5),COLUMN_UPDATED_ON(6);
	
	private int index;
	private BirthDayConstant(int index) {
		this.index=index;
	}

}
