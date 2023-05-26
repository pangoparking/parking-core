package org.parking.service;

import java.time.LocalDateTime;

import org.parking.CarRedisEntity;
import org.parking.FineRedisEntity;
import org.parking.FinesRedisRepository;
import org.parking.model.CarData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ValidatorServiceImpl implements ValidatorService {
    
	@Autowired
	FinesRedisRepository repository;
	
	@Override
	@Transactional
	public CarData checkIfCarFined(CarData carData) {
		if (repository.findById(carData.carID).orElse(null)==null) {
			addFineToRedis(carData);
			return null;
		}
		return carData;
	}

	private void addFineToRedis(CarData carData) {
		
		try {
			FineRedisEntity redisFine = new FineRedisEntity(); 
			redisFine.CarId=carData.carID;
			redisFine.fined=true;
			redisFine.fineTime=LocalDateTime.now();
			repository.save(redisFine);
			log.trace("FineData added to RedisRepository carId:%d",redisFine.CarId);
		} catch (Exception e) {
			log.error("FineData not added to RedisRepository ", e.getMessage());
			
		}
		
	}

	
	
	

}
