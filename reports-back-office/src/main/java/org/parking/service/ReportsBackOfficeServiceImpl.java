package org.parking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.parking.FineDoc;
import org.parking.repository.FinesRepository;
import org.parking.model.EnumStatus;
import org.parking.model.MonthlyFineCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ReportsBackOfficeServiceImpl implements ReportsBackOfficeService{
	
	@Autowired
	FinesRepository repository;
	
	private static final String FROM_TO_MSG = "from {} to {} ";
	private static final String MSG_FINES = "{}:{}, fines: {}";
	private static final String NOT_EXIST_MSG = "%s id: %d does not exist";
	private static final String MSG_STATUS_FINES = "status:{}, fines: {}";
	private static final String MSG_YEAR_FINES = "year:{}, fines: {}";
	private static final String FINE_NOT_EXIST_MSG = "fine id: %d does not exist";
	private static final String MSG_FINE_BY_ID = "fine: {}";
	private static final String FINE_UPDATED_MSG = "fine has been updated: {}";

	@Override
	public FineDoc getFineByID(long id) {
		FineDoc fine = repository.findById(id).orElse(null);
		if(fine == null) {
			String msg = String.format(FINE_NOT_EXIST_MSG, id);
			log.error(msg);
			throw new NoSuchElementException(msg);
		}
		log.debug(MSG_FINE_BY_ID, fine);
		return fine;
	}
	
	@Override
	public List<FineDoc> getFinesByKeyAndID(String key, long id) {
		checkExistOfIDByKey(key, id);
		List<FineDoc> fines = repository.findAllByKeyAndID(key, id);
		log.debug(MSG_FINES, key, id, fines);
		return fines;
	}

	@Override
	public List<FineDoc> getFinesByKeyAndIDAndDateTimeInterval(String key, long id, LocalDateTime from,
			LocalDateTime to) {
		checkExistOfIDByKey(key, id);
		List<FineDoc> fines = repository.findAllByKeyAndIDAndDateTimeInterval(key, id, from, to);
		log.debug(FROM_TO_MSG + MSG_FINES, from, to, id, fines);
		return fines;
	}

	@Override
	public List<FineDoc> getFinesByStatus(String status) {
		List<FineDoc> fines;
		if (status.compareTo("all") == 0) {
			fines = repository.findAll();
		} else {
			fines = repository.findAllByStatus(status);
		}
		log.debug(MSG_STATUS_FINES, status, fines);
		return fines;
	}

	@Override
	public List<FineDoc> getFinesByStatusAndDateTimeInterval(String status, LocalDateTime from, LocalDateTime to) {
		List<FineDoc> fines;
		if (status.compareTo("all") == 0) {
			fines = repository.findAllByDateTimeInterval(from, to);
		} else {
			fines = repository.findAllByStatusAndDateTimeInterval(status, from, to);
		}
		log.debug(FROM_TO_MSG + MSG_STATUS_FINES, from, to, status, fines);
		return fines;
	}

	@Override
	public Map<Integer, Integer> getFinesByYear(int year) {
		LocalDateTime from = LocalDateTime.of(year, 1, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(year + 1, 1, 1, 0, 0);
		List<MonthlyFineCount> fineCounts = repository.findAllByYear(from, to);
		Map<Integer, Integer> result = fineCounts.stream()
		        .collect(Collectors.toMap(MonthlyFineCount::getMonth, MonthlyFineCount::getCount));
		log.debug(MSG_YEAR_FINES, year, result.toString());
		return result;
	}

	@Transactional
	@Override
	public FineDoc updateFineStatus(long id, EnumStatus status) {
		FineDoc fine = getFineByID(id);
		fine.setStatus(status.name());
		repository.save(fine);
		log.debug(FINE_UPDATED_MSG, fine);
		return fine;
	}
	
	private void checkExistOfIDByKey(String key, long id) {
		List<FineDoc> res = null;
		res = repository.existsByKeyAndID(key, id, PageRequest.of(0, 1));
		if(res.isEmpty()) {
			String msg = String.format(NOT_EXIST_MSG, key, id);
			log.error(msg);
			throw new NoSuchElementException(msg);
		}
	}
	
}