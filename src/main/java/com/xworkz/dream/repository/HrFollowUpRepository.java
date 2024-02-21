package com.xworkz.dream.repository;

import java.util.List;

public interface HrFollowUpRepository {

	 boolean saveHrFollowUpDetails(List<Object> listitem) ;

	List<List<Object>> readFollowUpDetailsById();
}
