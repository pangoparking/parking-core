package org.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parking.model.CarData;
import org.parking.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class ValidatorServiceTest {
	@Autowired
	InputDestination producer;
	
     @Autowired
	 ValidatorService service;
     
     @MockBean
 	 FinesRedisRepository repository;
     
     static long CAR_FINED_ID=111;
     static long CAR_AREA=1515;
     static long CAR_NOT_FINED_ID=222;
     static FineRedisEntity finedCarEntity = new FineRedisEntity(CAR_FINED_ID,LocalDateTime.now().minusHours(1),true);
     
     
     @BeforeEach
     void beforeEachClass() throws Exception {
 		when(repository.findById(CAR_NOT_FINED_ID)).thenReturn(Optional.ofNullable(null));
 		when(repository.findById(CAR_FINED_ID)).thenReturn(Optional.of(finedCarEntity));
 		
 	}
	
    private static final String BINDING_NAME_VALIDATOR = "validatorConsumer-in-0";
	
    	
	@Test
	void ValidatorServiceTest_If_Car_Fined() {
		CarData carDataFined = new CarData(CAR_FINED_ID,CAR_AREA);
		producer.send(new GenericMessage<CarData>(carDataFined),BINDING_NAME_VALIDATOR);
		CarData responseCarData = service.checkIfCarFined(carDataFined);    
		assertEquals(responseCarData.carID, carDataFined.carID);
	}
	
	@Test
	void ValidatorServiceTest_If_Car_Not_Fined() {
		CarData carData = new CarData(CAR_NOT_FINED_ID,CAR_AREA);
		producer.send(new GenericMessage<CarData>(carData),BINDING_NAME_VALIDATOR);
		CarData responseCarData = service.checkIfCarFined(carData);    
		assertNull(responseCarData);
	}
	
	
}



	
	
    