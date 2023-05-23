package org.parking.service;

import java.time.LocalDateTime;

import org.parking.CarRedisEntity;
import org.parking.CarsOnParkingRepository;
import org.parking.model.CarData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ParkingAnalizerServiceImpl implements ParkingAnalizerService {
	
	@Autowired
	CarsOnParkingRepository repo;
	
	@Override
	@Transactional
	public CarData checkIfCarTimeOnParkingExpired(CarData carData) {
		CarData res = null;
		log.trace("ParkingAnalizerServiceImpl : isParkingTimeExpired : car [{}] is to be checked", carData);
		CarRedisEntity carRedisEntity = repo.findById(carData.carID).orElse(null);
		if (carRedisEntity == null) {
			log.trace(" {} is found in Redis DB", carData);
			addNewCarToParkingDb(carData);
		} else if (isTimeExpired(carRedisEntity)){
			log.trace("Car with number {} has expired time - sending to Inspector", carData.carID);
			res = carData;
		} else {
			log.trace("Car with number {} can stay on the parking till next check", carData.carID);
		}
		return res;
	}

	private boolean isTimeExpired(CarRedisEntity carRedisEntity) {
		if (carRedisEntity.getParkingEndTime().isBefore(LocalDateTime.now())) {
			log.trace("Time of car with number {} is {} and is expired",carRedisEntity.getCarID(), carRedisEntity.getParkingEndTime());
			return true;
		} else {
			log.trace("Time of car with number {} is {} and is NOT expired",carRedisEntity.getCarID(), carRedisEntity.getParkingEndTime());
			return false;
		}
		
	}

	private void addNewCarToParkingDb(CarData carData) {
		var timeToBeSet = LocalDateTime.now().plusMinutes(10);
		CarRedisEntity carOnParkingEntity = new CarRedisEntity(carData.carID, timeToBeSet);
		repo.save(carOnParkingEntity);
		log.trace("Car with number {} is added to Redis DB with end time {};", carData.carID, timeToBeSet);
	}
}
