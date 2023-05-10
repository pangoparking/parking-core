package org.parking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.parking.FineDoc;
import org.parking.repository.FinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class FinesCarService implements FinesByIDService {
	
	private static final String MSG_FINES = "car ID:{}, fines: {}";
	private static final String NOT_EXIST_MSG = "car id: %d does not exist";
	
	@Autowired
	FinesRepository repository;
	
	@Override
	public List<FineDoc> getFinesByID(long carID) {
		chekCarID(carID);
		List<FineDoc> fines = repository.findAllByCarID(carID);
		log.debug(MSG_FINES, carID, fines);
		return fines;
	}

	@Override
	public List<FineDoc> getFinesByIDAndDateTimeInterval(long carID, LocalDateTime from, LocalDateTime to) {
		chekCarID(carID);
		List<FineDoc> fines;
		try {
			fines = repository.findAllByCarIDAndDateTimeInterval(carID, from, to);
		} catch (Exception e) {
			fines = List.of();
		}
		log.debug("from {} to {} " + MSG_FINES, from, to, carID, fines);
		return fines;
	}

	private void chekCarID(long carID) {
		if(!repository.existsByCarID(carID)) {
			String msg = String.format(NOT_EXIST_MSG, carID);
			log.error(msg);
			throw new NoSuchElementException(msg);
		}
	}
	
}
