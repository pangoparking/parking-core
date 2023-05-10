package org.parking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.parking.FineDoc;
import org.parking.repository.FinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FinesStatusService {

	@Autowired
	FinesRepository repository;
	
	public List<FineDoc> getFinesByStatus(String status) {
		List<FineDoc >fines = repository.findAllByStatus(status);
		log.debug("status: {} fines: {}", status, fines);
		return fines;
	}

	public List<FineDoc> getFinesByStatusAndDateTimeInterval(String status, LocalDateTime from, LocalDateTime to) {
		List<FineDoc >fines = repository.findAllByStatusAndDateTimeInterval(status, from, to);
		log.debug("status: {} fines: {}", status, fines);
		return fines;
	}

}
