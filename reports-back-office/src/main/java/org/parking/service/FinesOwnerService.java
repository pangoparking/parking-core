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
public class FinesOwnerService implements FinesByIDService{
	
	private static final String MSG_FINES = "owner ID:{}, fines: {}";
	private static final String NOT_EXIST_MSG = "owner id: %d does not exist";
	
	@Autowired
	FinesRepository repository;
	
	@Override
	public List<FineDoc> getFinesByID(long ownerID) {
		chekOwnerID(ownerID);
		List<FineDoc> fines = repository.findAllByOwnerID(ownerID);
		log.debug(MSG_FINES, ownerID, fines);
		return fines;
	}

	@Override
	public List<FineDoc> getFinesByIDAndDateTimeInterval(long ownerID, LocalDateTime from, LocalDateTime to) {
		chekOwnerID(ownerID);
		List<FineDoc> fines;
		try {
			fines = repository.findAllByOwnerIDAndDateTimeInterval(ownerID, from, to);
		} catch (Exception e) {
			fines = List.of();
		}
		log.debug("from {} to {} " + MSG_FINES, from, to, ownerID, fines);
		return fines;
	}

	private void chekOwnerID(long ownerID) {
		if(!repository.existsByOwnerID(ownerID)) {
			String msg = String.format(NOT_EXIST_MSG, ownerID);
			log.error(msg);
			throw new NoSuchElementException(msg);
		}
	}
	
}
