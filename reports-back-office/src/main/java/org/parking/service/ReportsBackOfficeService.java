package org.parking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.parking.FineDoc;

public interface ReportsBackOfficeService {

	List<FineDoc> getFinesByOwnerID(long ownerID);
	List<FineDoc> getFinesByOwnerIDAndDateTimeInterval(long ownerID, LocalDateTime from, LocalDateTime to);
	
}
