package org.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parking.model.CarData;
import org.parking.model.EnumStatus;
import org.parking.service.InspectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class InspectorServiceTest {
	 public long CAR_ID_TIME_OUT = 222;
	 public long CAR_ID_TIME_PREPAID = 555;
	 public long PARKING_ID = 67;
	 public LocalDateTime TIME_OUT=LocalDateTime.now().minusHours(1);
	 public LocalDateTime TIME_PREPAID=LocalDateTime.now().plusHours(1);
	 
	
	
	@Autowired
	InspectorService inspectorService;
	  

	//@Test
	void InspectorService_PangoTimeOut() {
		var carData = new CarData(CAR_ID_TIME_OUT, PARKING_ID);
		var serviceResponce = inspectorService.isPangoPayed(carData);
		assertEquals(serviceResponce,EnumStatus.unpaid);
	}


	
}
