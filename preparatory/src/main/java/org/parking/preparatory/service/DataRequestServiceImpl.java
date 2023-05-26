package org.parking.preparatory.service;

import org.parking.model.CarData;
import org.parking.model.RawSqlData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

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
			rawSqlData = getData(carData).block();
			log.trace("getDataFromCODP : rawSqlData={}", rawSqlData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rawSqlData;
	}

	private Mono<RawSqlData> getData(CarData carData) {
		return client
				.method(HttpMethod.GET)
				.uri(urn)
				.body(Mono.just(carData), CarData.class)
				.retrieve()
				.bodyToMono(RawSqlData.class);
	};

	@PostConstruct
	void createWebClient() {
		this.client = WebClient.create(url);
	}
}
