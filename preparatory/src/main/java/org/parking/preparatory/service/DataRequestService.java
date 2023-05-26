package org.parking.preparatory.service;

import org.parking.model.CarData;
import org.parking.model.RawSqlData;
import org.springframework.web.reactive.function.client.WebClient;

public interface DataRequestService {
	RawSqlData getDataFromCODP(CarData carData);
}
