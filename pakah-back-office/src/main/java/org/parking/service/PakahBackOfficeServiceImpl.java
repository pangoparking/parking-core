package org.parking.service;

import org.parking.model.CarData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class PakahBackOfficeServiceImpl implements PakahBackOfficeService {

	@Value("${app.back-office.pakahDataHandler.url:http://localhost:8500}")
	public String url;
	@Value("${app.back-office.pakahDataHandler.urn:/pakahdh}")
	public String urn;

	WebClient client;

	@Override
	public CarData sendDataToDataHandler(long id, long parkingId) {
		log.trace("PakahBackOfficeServiceImpl : sendDataToDataHandler : id={}, parkingId={}", id, parkingId);
		var carData = new CarData(id, parkingId);
		log.trace("sendDataToDataHandler : carData={}", carData);
		var sentData = sendData(carData).block().getBody();
		if (sentData == null) {
			log.error("sPakahBackOfficeServiceImpl : sendDataToDataHandler : sentData is null");
			throw new NullPointerException("PakahBackOfficeServiceImpl : sendDataToDataHandler : sentData is null");
		}
		log.trace("sendDataToDataHandler : sentData={}", sentData);
		return sentData;
	}

	@Override
	public void createWebClientForTesting(String uri) {
		log.debug("PakahBackOfficeServiceImpl : createWebClientForTesting : uri ={}", uri);
		this.client = WebClient.create(uri);
	}

	public Mono<ResponseEntity<CarData>> sendData(CarData carData) {
		return client.method(HttpMethod.PUT)
				.body(Mono.just(carData), CarData.class)
				.retrieve()
				.toEntity(CarData.class);
	};

	@PostConstruct
	private void setWebClient() {
		client = WebClient.create(url + urn);
	}

}
