package org.parking.service;

import java.time.LocalDateTime;

import org.parking.CarRedisEntity;
import org.parking.CarsOnParkingRepository;
import org.parking.model.CarData;
import org.parking.model.EnumStatus;
import org.parking.model.ParkingTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class InspectorServiceImpl implements InspectorService {
	
	
	@Autowired
	CarsOnParkingRepository repository;
	
	@Autowired
	Environment env;
	
	@Override
	@Transactional
	public EnumStatus isPangoPayed(CarData car) {

		RestTemplate restTemplate = new RestTemplate();
		EnumStatus status = EnumStatus.canceled;
		
		ParkingTime responseFromPango = restTemplate.getForObject(
			String.format("http://localhost:%s/car_check/%d",env.getProperty("server.port"),car.carID), ParkingTime.class);
		
        CarRedisEntity carRedis = new CarRedisEntity();

		
		status = (responseFromPango.parkingEndTime != null && 
				responseFromPango.parkingEndTime.compareTo(LocalDateTime.now()) > 0) 
				? EnumStatus.paid : EnumStatus.unpaid;
		if (status == EnumStatus.paid) {
			repository.save(carRedis);
			log.trace("Sent data to Redis: carID %d %s", carRedis.carID, carRedis.parkingEndTime.toString());
		}
		
	return status;
	}

	}
