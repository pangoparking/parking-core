package org.parking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.parking.FineDoc;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportsBackOfficeServiceImpl implements ReportsBackOfficeService {

	private ApplicationContext applicationContext;
	
	@Autowired
	FinesStatusService finesStatusService; 
	
	private static final String TO = "to";
	private static final String FROM = "from";
	private static final String FROM_TO_MSG = " from {} to {}";
	private static final String GET_FINES_BY_ID_MSG = "calling the service to get fines on {}: {}";
	private static final String GET_FINES_BY_STATUS_MSG = "calling the service to get fines on status: {}";
	private static final String CLASS_NAME_MSG = "constructed class name according to request: '{}'";
	private static final String DATE_ERROR_MSG = "date 'from' %s can't be after date 'to' %s";

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;		
	}
	
	@Override
	public List<FineDoc> getFinesByKeyAndID(String key, long id, String fromDateTime, String toDateTime) {
		if(fromDateTime == null && toDateTime == null) {
			FinesByIDService service = (FinesByIDService) getService(key);
			log.debug(GET_FINES_BY_ID_MSG, key, id);
			return service.getFinesByID(id);
		}
		Map<String, LocalDateTime> dates = checkAndGetDates(fromDateTime, toDateTime);
		FinesByIDService service = (FinesByIDService) getService(key);
		log.debug(GET_FINES_BY_ID_MSG + FROM_TO_MSG, key, id, dates.get(FROM), dates.get(TO));
		return service.getFinesByIDAndDateTimeInterval(id, dates.get(FROM), dates.get(TO));
	}

	@Override
	public List<FineDoc> getFinesByStatus(String status, String fromDateTime, String toDateTime) {
		if(fromDateTime == null && toDateTime == null) {
			log.debug(GET_FINES_BY_STATUS_MSG, status);
			return finesStatusService.getFinesByStatus(status);
		}
		Map<String, LocalDateTime> dates = checkAndGetDates(fromDateTime, toDateTime);
		log.debug(GET_FINES_BY_STATUS_MSG + FROM_TO_MSG, status, dates.get(FROM), dates.get(TO));
		return finesStatusService.getFinesByStatusAndDateTimeInterval(status, dates.get(FROM), dates.get(TO));
	}

	@Override
	public String classNameBuilder(String key) {
		String className = "fines" + key.substring(0, 1).toUpperCase() + key.substring(1) + "Service";  
		log.debug(CLASS_NAME_MSG, className);
		return className;
	}

	private Map<String, LocalDateTime> checkAndGetDates(String fromDateTime, String toDateTime) {
		LocalDateTime from = fromDateTime == null ? LocalDateTime.of(1000, 1, 1, 0, 0) : LocalDateTime.parse(fromDateTime);
		LocalDateTime to = toDateTime == null ? LocalDateTime.now() : LocalDateTime.parse(toDateTime);
		Map<String, LocalDateTime> dates = Map.of(FROM, from, TO, to);
		if(from.isAfter(to)) {
			String msg = String.format(DATE_ERROR_MSG, from, to);
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		return dates;
	}

	private Object getService(String key) {
		String className = classNameBuilder(key);
		Object service = applicationContext.getBean(className);
		return service;
	}	

}
