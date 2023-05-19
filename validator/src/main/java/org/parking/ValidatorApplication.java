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

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
public class ValidatorApplication {

	@Autowired 
	ValidatorService service;
	
	@Autowired
	StreamBridge streamBridge;
	
	@Value("${app.validator.binding.name:fineValidator-out-0}")
	private String bindingName;
	
	public static void main(String[] args) {
		SpringApplication.run(ValidatorApplication.class, args);

	}
	
	@Bean
	Consumer<CarData> validatorConsumer() {
		return this::fineValidator;
	}
	void fineValidator(CarData car) {
		if(service.checkIfCarFined(car)!=null) {
			try {
				streamBridge.send(bindingName, car);
			} catch (Exception e) {
				log.error("Sending message to middleware service from validator failed:",e.getMessage());
			}
		}
		
	}

}
