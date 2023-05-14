package org.parking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.parking.FineDoc;
import org.parking.model.EnumStatus;

public interface ReportsBackOfficeService{

	FineDoc getFineByID(long id);
	List<FineDoc> getFinesByKeyAndID(String key, long id);
	List<FineDoc> getFinesByKeyAndIDAndDateTimeInterval(String key, long id, LocalDateTime form, LocalDateTime to);
	List<FineDoc> getFinesByStatus(String status);
	List<FineDoc> getFinesByStatusAndDateTimeInterval(String status, LocalDateTime from, LocalDateTime to);
	Map<Integer, Integer> getFinesByYear(int year);
	FineDoc updateFineStatus(long id, EnumStatus status);
	
}
