package org.parking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.parking.FineDoc;

public interface FinesByIDService {

	List<FineDoc> getFinesByID(long id);
	List<FineDoc> getFinesByIDAndDateTimeInterval(long id, LocalDateTime from, LocalDateTime to);

}
