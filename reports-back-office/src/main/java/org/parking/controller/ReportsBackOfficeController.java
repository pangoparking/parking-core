package org.parking.controller;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.parking.FineDoc;
import org.parking.model.EnumStatus;
import org.parking.model.StatusUpdateDto;
import org.parking.service.ReportsBackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("fines")
@Slf4j
@Validated
public class ReportsBackOfficeController {

	private static final String GET_FINES_BY_KEY_AND_ID_MSG = "received request to get fines on {} id {}";
	private static final String VALID_ID_MSG = "id can't be null or less than 1";
	private static final String REGEXP_DATE = "\\d{4}-(0\\d|1[012])-(0\\d|[12]\\d|3[01])T\\d{2}:\\d{2}(:\\d{2})?(.\\d{3})?";
	private static final String REGEXP_DATE_MSG = "date should be in format YYYY-MM-DDThh:mm(:ss.mll)";
	private static final String GET_FINES_BY_STATUS_MSG = "received request to get fines on status: {}";private static final String TO = "to";
	private static final String FROM = "from";
	private static final String FROM_TO_MSG = " from {} to {}";
	private static final String DATE_ERROR_MSG = "date 'from' %s can't be after date 'to' %s";
	private static final String REGEXP_KEY = "ownerID||carID";
	private static final String REGEXP_KEY_MSG = "key should be one of:'ownerID','carID'";
	private static final String REGEXP_STATUS = "all||paid||unpaid||canceled";
	private static final String REGEXP_STATUS_MSG = "status should be one of: all, paid, unpaid, canceled";
	private static final String VALID_YEAR_MSG = "year can't be null or less than 2000";
	private static final String GET_FINES_BY_YEAR_MSG = "received request to get fines for year {}";
	private static final String GET_FINE_BY_ID_MSG = "received request to get fine on id {}";
	private static final String UPDATE_FINE_STATUS_MSG = "received request to update fine status on id {}";
	
	@Autowired
	ReportsBackOfficeService service;
	
	@GetMapping("/id/{id}")
	FineDoc getFineByID(@Min(value = 1, message = VALID_ID_MSG) @PathVariable ("id") long id) {
		log.debug(GET_FINE_BY_ID_MSG, id);
		return service.getFineByID(id);
	}

	@GetMapping("{key}/{id}")
	List<FineDoc> getFinesByKeyAndID(@Pattern(regexp = REGEXP_KEY, message = REGEXP_KEY_MSG) @PathVariable ("key") String key,
			@Min(value = 1, message = VALID_ID_MSG) @PathVariable ("id") long id,
			@Pattern(regexp = REGEXP_DATE, message = REGEXP_DATE_MSG) @RequestParam(name = "from", required = false) String fromDateTime,
			@Pattern(regexp = REGEXP_DATE, message = REGEXP_DATE_MSG) @RequestParam(name = "to", required = false) String toDateTime) {
		if(fromDateTime == null && toDateTime == null) {
			log.debug(GET_FINES_BY_KEY_AND_ID_MSG, key, id);
			return service.getFinesByKeyAndID(key, id);
		}
		Map<String, LocalDateTime> dates = checkAndGetDates(fromDateTime, toDateTime);
		log.debug(GET_FINES_BY_KEY_AND_ID_MSG + FROM_TO_MSG, key, id, dates.get(FROM), dates.get(TO));
		return service.getFinesByKeyAndIDAndDateTimeInterval(key, id, dates.get(FROM), dates.get(TO));
	}
	
	@GetMapping("{status}")
	List<FineDoc> getFinesByStatus(@Pattern(regexp = REGEXP_STATUS, message = REGEXP_STATUS_MSG) @PathVariable(name = "status") String status,
			@Pattern(regexp = REGEXP_DATE, message = REGEXP_DATE_MSG) @RequestParam(name = "from", required = false) String fromDateTime,
			@Pattern(regexp = REGEXP_DATE, message = REGEXP_DATE_MSG) @RequestParam(name = "to", required = false) String toDateTime) {
		if(fromDateTime == null && toDateTime == null) {
			log.debug(GET_FINES_BY_STATUS_MSG, status);
			return service.getFinesByStatus(status);
		}
		Map<String, LocalDateTime> dates = checkAndGetDates(fromDateTime, toDateTime);
		log.debug(GET_FINES_BY_STATUS_MSG + FROM_TO_MSG, status, dates.get(FROM), dates.get(TO));
		return service.getFinesByStatusAndDateTimeInterval(status, dates.get(FROM), dates.get(TO));
	}
	
	@GetMapping("year/{year}")
	Map<Integer, Integer> getFinesByYear(@Min(value = 2000, message = VALID_YEAR_MSG) @PathVariable ("year") int year) {
		log.debug(GET_FINES_BY_YEAR_MSG, year);
		return service.getFinesByYear(year);
	}
	
	@PutMapping("update")
	FineDoc updateFineStatus(@RequestBody @Valid StatusUpdateDto statusUpdateDto) {
		log.debug(UPDATE_FINE_STATUS_MSG, statusUpdateDto.id, statusUpdateDto.status);
		return service.updateFineStatus(statusUpdateDto.id, EnumStatus.valueOf(statusUpdateDto.status));
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
	
}
