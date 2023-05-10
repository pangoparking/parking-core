package org.parking.service;

import java.util.List;

import org.parking.FineDoc;
import org.springframework.context.ApplicationContextAware;

public interface ReportsBackOfficeService extends ApplicationContextAware{

	List<FineDoc> getFinesByKeyAndID(String key, long id, String fromDateTime, String toDateTime);
	String classNameBuilder(String key);
	List<FineDoc> getFinesByStatus(String status, String fromDateTime, String toDateTime);
	
}
