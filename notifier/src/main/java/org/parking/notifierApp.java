package org.parking;

import java.util.function.Consumer;

import org.parking.model.CarData;
import org.parking.model.Fine;
import org.parking.service.NotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
public class notifierApp {

	@Autowired
	NotifierService service;
	
	@Autowired
	StreamBridge streamBridge;
	
	@Value("${app.notifier.binding.name:notifier-out-0}")
	private String bindingName;
	
	public static void main(String[] args) {
		SpringApplication.run(notifierApp.class,args);
	
	}
	
	@Bean
	Consumer<Fine> notifierConsumer(){
		return this::sendNotification; 
	}
	
	void sendNotification(Fine fine) {
		fine = service.SendMailService(fine);
		if(fine != null) {
			streamBridge.send(bindingName, fine);
		
		}

	}
}
