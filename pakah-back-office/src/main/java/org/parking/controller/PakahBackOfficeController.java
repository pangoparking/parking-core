package org.parking.controller;

import org.parking.model.CarData;
import org.parking.service.PakahBackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("pakah")
public class PakahBackOfficeController {

		private static final String VALID_ID_MSG = "Id can't be null or less than 1";
		private static final String VALID_PARKING_ID_MSG = "parkingId shold be in range [1 -3]";
		
		@Autowired
		PakahBackOfficeService service;

	@GetMapping
	CarData transferData (@Min(value = 1, message = VALID_ID_MSG) @PathVariable ("id") long id, 
			@Min(value = 1, message = VALID_PARKING_ID_MSG) @PathVariable ("parkingId") long parkingId) {
		log.trace("Data recieved from pakah : (car id) Id={}, (parking lot id) parkingId={}", id,  parkingId);
		var res =  service.sendDataToDataHandler(id, parkingId);
		log.trace("Data sent to application : {}", res);
		return res;
	}
}
