package com.xworkz.dream.constants;

public enum AttendanceConstant {

	COLUMN_ATTENDANCID(0), COLUMN_ID(1), COLUMN_TRAINEE_NAME(2), COLUMN_COURSE(3), COLUMN_TOTAL_ABSENT(4),
	COLUMN_ABSENT_DATE(5), COLUMN_REASON(6), COLUMN_CREATED_BY(7), COLUMN_CREATED_ON(8), COLUMN_UPDATED_BY(9),
	COLUMN_UPDATED_ON(10);

	private int index;

	private AttendanceConstant(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
