package org.parking.preparatory.service;

import org.parking.model.CarData;
import org.parking.model.RawSqlData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DataRequestServiceImpl implements DataRequestService {

	@Value("${app.preparatory.CODP.url:http://localhost:8400}")
	private String url;
	@Value("${app.preparatory.CODP.urn:/parking/getCarInfo}")
	private String urn;

	WebClient client;

	@Override
	public RawSqlData getDataFromCODP(CarData carData) {
		log.trace("DataRequestServiceImpl : getDataFromCODP : carData={}", carData);
		RawSqlData rawSqlData = null;
		try {
			client = WebClient.create(url);
			log.trace("getDataFromCODP : client.toString={}", client.toString());
			rawSqlData = getData(carData);
			log.trace("getDataFromCODP : rawSqlData={}", rawSqlData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rawSqlData;
	}

	private RawSqlData getData(CarData carData) {

		return client.get().uri(urn).attribute("attributeName", carData).retrieve().bodyToMono(RawSqlData.class)
				.block();
	};

}
