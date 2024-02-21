package com.xworkz.dream.constants;

public enum RegistrationConstant {
	COLUMN_ID(0), COLUMN_TRAINEE_NAME(1), COLUMN_EMAIL(2), COLUMN_CONTACT_NUMBER(3), COLUMN_DATE_OF_BIRTH(4),
	COLUMN_QUALIFICATION(5), COLUMN_STREAM(6), COLUMN_YEAR_OF_PASSOUT(7), COLUMN_COLLEGE_NAME(8), COLUMN_COURSE(9),
	COLUMN_BRANCH(10), COLUMN_TRAINER_NAME(11), COLUMN_BATCH_TYPE(12), COLUMN_BATCH_TIMING(13), COLUMN_START_DATE(14),
	COLUMN_OFFERED_AS(15), COLUMN_REFERRAL_NAME(16), COLUMN_REFERRAL_CONTACT_NUMBER(17), COLUMN_COMMENTS(18),
	COLUMN_XWORKZ_EMAIL(19), COLUMN_WORKING(20), COLUMN_PREFERRED_LOCATION(21), COLUMN_PREFERRED_CLASS_TYPE(22),
	COLUMN_SEND_WHATSAPP_LINK(23), COLUMN_REGISTRATION_DATE(24), COLUMN_CREATED_BY(25), COLUMN_CREATED_ON(26),
	COLUMN_UPDATED_BY(27), COLUMN_UPDATED_ON(28), COLUMN_USN_NUMBER(29), COLUMN_ALTERNATIVE_CONTACT_NUMBER(30),
	COLUMN_UNIQUE_ID(31), COLUMN_CSR_FLAG(32), COLUMN_ACTIVE_FLAG(33),COLUMN_SSLC(34),COLUMN_PUC(35),COLUMN_DEGREE(36);

	private int index;

	private RegistrationConstant(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
