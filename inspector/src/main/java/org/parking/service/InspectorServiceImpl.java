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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class InspectorServiceImpl implements InspectorService {
	

	@Autowired
	CarsOnParkingRepository repository;

	@Autowired
	Environment env;
	
//	private final CarsOnParkingRepository repository;
//    private final Environment env;
    private final RestTemplate restTemplate;
    
    @Autowired
    public InspectorServiceImpl( RestTemplate restTemplate) {
     //   this.repository = repository;
   //     this.env = env;
       this.restTemplate = restTemplate;
    }
	
	@Override
	@Transactional
	public EnumStatus isPangoPayed(CarData car) {

		//RestTemplate restTemplate = new RestTemplate();
		
		EnumStatus status = null;
		
		ParkingTime responseFromPango;
		CarRedisEntity carRedis = new CarRedisEntity();
		try {
			responseFromPango = restTemplate.getForObject(
				String.format("http://localhost:%s/car_check/%d",env.getProperty("server.port"),car.carID), ParkingTime.class);
			
			status = (responseFromPango.parkingEndTime != null && 
					responseFromPango.parkingEndTime.compareTo(LocalDateTime.now()) > 0) 
					? EnumStatus.paid : EnumStatus.unpaid;
			carRedis.carID=car.carID;
			carRedis.parkingEndTime=responseFromPango.parkingEndTime;
			
		} catch (RestClientException e) {
			log.error("Response from Pango service FAILED",e.getMessage());
		}
		
		if (status == EnumStatus.paid) {
			try {
				repository.save(carRedis);
				log.trace("Sent data to Redis: carID %d %s", carRedis.carID, carRedis.parkingEndTime.toString());
			} catch (Exception e) {
				log.error("Sent data to Redis FAILED: carID %d %s", carRedis.carID, carRedis.parkingEndTime.toString(),e.getMessage());
			}
		}
		
	
	return status;
		
	}

	}
