package org.parking;

import org.parking.model.CarData;
import org.parking.service.ParkingAnalizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import java.util.function.Consumer;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
public class ParkingAnalizerAppl {
	
	@Value("${app.jumps.binding.name:carAnalyzer-out-0}")
	private String bindingName;
	
	@Autowired
	ParkingAnalizerService service;
	
	@Autowired
	StreamBridge streamBridge;

	public static void main(String[] args) {
		SpringApplication.run(ParkingAnalizerAppl.class, args);
	}
	
	@Bean
	Consumer<CarData> analyzerConsumer() {
		return this::carAnalyzer;
	}
	void carAnalyzer(CarData car) {
		car = service.checkIfCarTimeOnParkingExpired(car);
		if(car != null) {
			streamBridge.send(bindingName, car);
		}
		
	}
	

}
