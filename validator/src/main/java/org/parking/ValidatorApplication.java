package org.parking;

import java.util.function.Consumer;

import org.parking.model.CarData;
import org.parking.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ValidatorApplication {

	@Autowired 
	ValidatorService service;
	
	@Autowired
	StreamBridge streamBridge;
	
	@Value("${app.validator.binding.name:carValidator-out-0}")
	private String bindingName;
	
	public static void main(String[] args) {
		SpringApplication.run(ValidatorApplication.class, args);

	}
	
	@Bean
	Consumer<CarData> validatorConsumer() {
		return this::fineValidator;
	}
	void fineValidator(CarData car) {
		car = service.checkIfCarFined(car);
		if(car != null) {
			streamBridge.send(bindingName, car);
		}
		
	}

}
