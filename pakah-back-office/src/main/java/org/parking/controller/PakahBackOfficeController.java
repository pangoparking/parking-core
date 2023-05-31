package org.parking.controller;

import org.hibernate.validator.constraints.Range;
import org.parking.model.CarData;
import org.parking.service.PakahBackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/pakah")
@Validated
public class PakahBackOfficeController {

		private static final String VALID_ID_MSG = "car number should contain 8 digits";
		private static final String VALID_PARKING_ID_MSG = "parkingId shold be in range [1 - 3]";
		
		@Autowired
		PakahBackOfficeService service;

	@PutMapping("/{id}/{parkingId}")
	CarData transferData (
			@Range (min = 10000000, max = 99999999, message = VALID_ID_MSG)
			@PathVariable(required = true, value = "id") long id, 
			@Range (min = 1, max = 3, message = VALID_PARKING_ID_MSG)
			@PathVariable(required = true, value = "parkingId") long parkingId
			) {
		log.trace("Data recieved from pakah : id={}, parkingId={}", id, parkingId);
		var res =  service.sendDataToDataHandler(Long.valueOf(id), Long.valueOf(parkingId));
		log.trace("Data sent to application : {}", res);
		return res;
	}
}
