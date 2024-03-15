package com.xworkz.dream.constants;

import lombok.Getter;

@Getter
public enum ClientFollowUpConstant {
	COLUMN_ID(0), COLUMN_HR_ID(1), COLUMN_ATTEMPT_ON(2), COLUMN_ATTEMPTED_BY(3), COLUMN_ATTEMPT_STATUS(4),
	COLUMN_CALL_DURATION(5), COLUMN_CALL_BACK_DATE(6), COLUMN_CALL_BACK_TIME(7), COLUMN_COMMENTS(8);

	private int index;

	private ClientFollowUpConstant(int index) {
		this.index = index;
	}

}
