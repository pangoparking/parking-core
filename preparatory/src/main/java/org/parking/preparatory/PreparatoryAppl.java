package org.parking.preparatory;

import java.util.function.Consumer;
import org.parking.model.*;
import org.parking.preparatory.service.DataRequestService;
import org.parking.preparatory.service.FinePreparatoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
public class PreparatoryAppl {

	@Autowired
	FinePreparatoryService finePreparatoryService;
	@Autowired
	DataRequestService dataRequestService;

	@Value("${app.preparatory.binding.name:preparatory-out-0}")
	String bindingName;

	
	@Autowired
	StreamBridge streamBridge;
	
	public static void main(String[] args) {
		SpringApplication.run(PreparatoryAppl.class, args);
	}

	@Bean
	Consumer<CarData> preparatoryConsumer() {
		return this::produceFine;
	}

	void produceFine(CarData carData) {
		log.trace("PreparatoryAppl : generateFine");
		if (carData == null) {
			log.debug("generateFine : carData is null");
			throw new NullPointerException();
		}
		var rawSqlData = dataRequestService.getDataFromCODP(carData);
		log.trace("generateFine : rawSqlData={}", rawSqlData);
		var fine = finePreparatoryService.produceFine(rawSqlData);
		log.trace("generateFine : fine={}", fine);
		streamBridge.send(bindingName, fine);
	}
}
