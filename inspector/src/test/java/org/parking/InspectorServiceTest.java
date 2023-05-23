package org.parking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.parking.model.CarData;
import org.parking.model.EnumStatus;
import org.parking.model.ParkingTime;
import org.parking.service.InspectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
//@Import(TestChannelBinderConfiguration.class)
public class InspectorServiceTest {
	
	//@Autowired
	//InputDestination producer;
	
	@MockBean
    private RestTemplate restTemplate;
	
	 @MockBean
	 CarsOnParkingRepository repository;

	@Autowired
	Environment env;
	 
	 @Autowired 
     InspectorService serviceInspector;

	 public long CAR_ID_TIME_OUT = 222;
	 public long CAR_ID_TIME_PREPAID = 555;
	 public long PARKING_ID = 67;
	 public LocalDateTime TIME_OUT=LocalDateTime.now().minusHours(1);
	 public LocalDateTime TIME_PREPAID=LocalDateTime.now().plusHours(1);
	
	
//	 private static final String BINDING_NAME_VALIDATOR = "inspectorConsumer-in-0";
		
 	
		
		@Test
	    public void InspectorServiceTest_ParkingPrepaid() {
	        CarData carData = new CarData(CAR_ID_TIME_PREPAID,PARKING_ID);

	       ParkingTime responseFromPango = new ParkingTime();
	       responseFromPango.parkingEndTime= TIME_PREPAID;
	       responseFromPango.carID=CAR_ID_TIME_PREPAID;
	       
	        when(restTemplate.getForObject(any(String.class), any())).thenReturn(responseFromPango);

	        CarRedisEntity carRedis = new CarRedisEntity(responseFromPango.carID,responseFromPango.parkingEndTime);

	        when(repository.save(any(CarRedisEntity.class))).thenReturn(carRedis);
 
	        
	        
	        EnumStatus status = serviceInspector.isPangoPayed(carData);
	        
            assertEquals(EnumStatus.paid, status);
	    }
	
	
		@Test
	    public void InspectorServiceTest_ParkingUnpaid() {
	        CarData carData = new CarData(CAR_ID_TIME_OUT,PARKING_ID);

	       ParkingTime responseFromPango = new ParkingTime();
	       responseFromPango.parkingEndTime= TIME_OUT;
	       responseFromPango.carID=CAR_ID_TIME_OUT;
	       
	        when(restTemplate.getForObject(any(String.class), any())).thenReturn(responseFromPango);

	        CarRedisEntity carRedis = new CarRedisEntity(responseFromPango.carID,responseFromPango.parkingEndTime);

	        when(repository.save(any(CarRedisEntity.class))).thenReturn(carRedis);
 
	        
	        
	        EnumStatus status = serviceInspector.isPangoPayed(carData);
	        
            assertEquals(EnumStatus.unpaid, status);
	    }
		
	
		
		
}

