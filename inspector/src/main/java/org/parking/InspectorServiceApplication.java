package org.parking;

import java.util.function.Consumer;

import org.parking.model.CarData;
import org.parking.model.EnumStatus;
import org.parking.service.InspectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import lombok.extern.log4j.Log4j2;


@Log4j2
@SpringBootApplication
public class InspectorServiceApplication {
	
	@Autowired
	InspectorService service;
	
	@Value("${app.inspector.binding.name:inspector-out-0}")
	private String bindingName;

	@Autowired
	StreamBridge streamBridge;
 
	public static void main(String[] args) {
		SpringApplication.run(InspectorServiceApplication.class, args);
	}
	
@Bean
Consumer<CarData> inspectorConsumer(){
	return this::payInspector; 
}

 void payInspector(CarData car) {
	 if (service.isPangoPayed(car)==EnumStatus.unpaid) {
	 streamBridge.send(bindingName,car); 
     System.err.println("Message sent"); 
	 }
 }	
 

	}

