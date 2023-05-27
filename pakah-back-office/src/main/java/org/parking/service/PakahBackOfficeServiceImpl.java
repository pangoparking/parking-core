package org.parking.service;

import org.parking.model.CarData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class PakahBackOfficeServiceImpl implements PakahBackOfficeService {
	
	@Value("${app.preparatory.CODP.url:http://localhost:8500}")
	private String url;
	@Value("${app.preparatory.CODP.urn:/pakahdh}")
	private String urn;
	
	WebClient client;
	
	@Override
	public CarData sendDataToDataHandler(long id, long parkingId) {
		log.trace("PakahBackOfficeServiceImpl : sendDataToDataHandler : id={}, parkingId={}", id, parkingId);
		var carData = new CarData(id, parkingId);
		log.trace("sendDataToDataHandler : carData={}", carData);
		var sentData = sendData(carData).block();
		log.trace("sendDataToDataHandler : sentData={}", sentData);
		return sentData;
	}
	
	private Mono<CarData> sendData(CarData carData) {
		return client
				.method(HttpMethod.GET)
				.uri(urn)
				.body(Mono.just(carData), CarData.class)
				.retrieve()
				.bodyToMono(CarData.class);
	};
	
	@PostConstruct
	void createWebClient() {
		this.client = WebClient.create(url);
	}
}
