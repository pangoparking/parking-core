package org.parking.controller;
import java.util.List;

import org.parking.FineDoc;
import org.parking.service.ReportsBackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("fines")
@Slf4j
@Validated
public class ReportsBackOfficeController {

	private static final String GET_FINES_BY_ID_MSG = "received request to get fines on {} id {}";
	private static final String VALID_ID_MSG = "id can't be null or less than 1";
	private static final String REGEXP_DATE = "\\d{4}-(0\\d|1[012])-(0\\d|[12]\\d|3[01])T\\d{2}:\\d{2}(:\\d{2})?(.\\d{3})?";
	private static final String REGEXP_DATE_MSG = "date should be in format YYYY-MM-DDThh:mm(:ss.mll)";
	private static final String GET_FINES_BY_STATUS_MSG = "received request to get fines on status: {}";
	
	@Autowired
	ReportsBackOfficeService service;

	@GetMapping("id/{key}/{id}")
	List<FineDoc> getFinesByID(@PathVariable ("key") String key, @Min(value = 1, message = VALID_ID_MSG) @PathVariable ("id") long id,
			@Pattern(regexp = REGEXP_DATE, message = REGEXP_DATE_MSG) @RequestParam(name = "from", required = false) String fromDateTime,
			@Pattern(regexp = REGEXP_DATE, message = REGEXP_DATE_MSG) @RequestParam(name = "to", required = false) String toDateTime) {
		log.debug(GET_FINES_BY_ID_MSG, key, id);
		return service.getFinesByKeyAndID(key, id, fromDateTime, toDateTime);
	}
	
	@GetMapping("{status}")
	List<FineDoc> getFinesByStatus(@PathVariable(name = "status") String status,
			@Pattern(regexp = REGEXP_DATE, message = REGEXP_DATE_MSG) @RequestParam(name = "from", required = false) String fromDateTime,
			@Pattern(regexp = REGEXP_DATE, message = REGEXP_DATE_MSG) @RequestParam(name = "to", required = false) String toDateTime) {
		log.debug(GET_FINES_BY_STATUS_MSG, status);
		return service.getFinesByStatus(status, fromDateTime, toDateTime);
	}
	
}
