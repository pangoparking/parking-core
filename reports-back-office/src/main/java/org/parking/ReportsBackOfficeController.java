package org.parking;

import java.time.LocalDateTime;
import java.util.List;

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

	private static final String GET_FINES_MSG = "received request to get fines for owner id {}";
	private static final String VALID_ID_MSG = "id can't be null or less than 1";
	private static final String REGEXP_DATE = "\\d{4}-(0\\d|1[012])-(0\\d|[12]\\d|3[01])T\\d{2}:\\d{2}(:\\d{2})?(.\\d{3})?";
	private static final String REGEXP_DATE_MSG = "date should be in format YYYY-MM-DDThh:mm(:ss.mll)";
	
	@Autowired
	ReportsBackOfficeService service;

	@GetMapping("{ownerID}")
	List<FineDoc> getFinesByOwnerID(@Min(value = 1, message = VALID_ID_MSG) @PathVariable ("ownerID") long ownerID,
			@Pattern(regexp = REGEXP_DATE, message = REGEXP_DATE_MSG) @RequestParam(name = "from", required = false) String fromDateTime,
			@Pattern(regexp = REGEXP_DATE, message = REGEXP_DATE_MSG) @RequestParam(name = "to", required = false) String toDateTime) {
		if(fromDateTime == null && toDateTime == null) {
			log.debug(GET_FINES_MSG, ownerID);
			return service.getFinesByOwnerID(ownerID);
		}
		LocalDateTime from = fromDateTime == null ? LocalDateTime.of(1000, 1, 1, 0, 0) : LocalDateTime.parse(fromDateTime);
		LocalDateTime to = toDateTime == null ? LocalDateTime.now() : LocalDateTime.parse(toDateTime);
		log.debug(GET_FINES_MSG +" from {} to {}", ownerID, from, to);
		return service.getFinesByOwnerIDAndDateTimeInterval(ownerID, from, to);
	}
	
}
