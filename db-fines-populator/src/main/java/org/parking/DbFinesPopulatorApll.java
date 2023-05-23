package org.parking;

import java.util.function.Consumer;

import org.parking.model.Fine;
import org.parking.repository.FinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class DbFinesPopulatorApll {
	
	@Autowired
	FinesRepository fineRepository;

	public static void main(String[] args) {
		SpringApplication.run(DbFinesPopulatorApll.class, args);
	}

	@Bean
	Consumer<Fine> finesConsumer() {
		return this::publishFine;
	}

	void publishFine(Fine fine) {
		log.debug("received fine(id: {}) for publishing to DataBase", fine.id);
		FineDoc fineDoc = FineDoc.of(fine);
		fineRepository.save(fineDoc);
	}
	
}
