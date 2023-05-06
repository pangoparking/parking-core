package org.parking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parking.model.CarData;
import org.parking.service.ParkingAnalizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ParkingAnalyzerServiceTest {

	private static final long CAR_ID_NO_DATA = 000;
	private static final long CAR_ID_PARKING_TIME_EXPIRED = 111;
	private static final long CAR_ID_PARKING_TIME_NOT_EXPIRED = 222;
	private static final long PARKING_ID = 0;

	@MockBean
	CarsOnParkingRepository repository;

	@Autowired
	ParkingAnalizerService service;

	static LocalDateTime CURRENT_TIME = LocalDateTime.now();
	static LocalDateTime CURRENT_TIME_MINUS_5_MIN = CURRENT_TIME.plusMinutes(5);
	static LocalDateTime CURRENT_TIME_PLUS_5_MIN = CURRENT_TIME.minusMinutes(5);

	static CarRedisEntity carNoData = new CarRedisEntity(CAR_ID_NO_DATA, CURRENT_TIME);
	static CarRedisEntity carParkingTimeExpired = new CarRedisEntity(CAR_ID_PARKING_TIME_EXPIRED,
			CURRENT_TIME_MINUS_5_MIN);
	static CarRedisEntity carParkingTimeNotExpired = new CarRedisEntity(CAR_ID_PARKING_TIME_NOT_EXPIRED,
			CURRENT_TIME_PLUS_5_MIN);

	@BeforeEach
	void setUpBeforeClass() throws Exception {
		when(repository.findById(CAR_ID_NO_DATA)).thenReturn(Optional.ofNullable(null));
		when(repository.findById(CAR_ID_PARKING_TIME_EXPIRED)).thenReturn(Optional.of(carParkingTimeExpired));
		when(repository.findById(CAR_ID_PARKING_TIME_NOT_EXPIRED)).thenReturn(Optional.of(carParkingTimeNotExpired));
	}

	@Test
	void commonMockingTest() {
		assertEquals(Optional.empty(), repository.findById(CAR_ID_NO_DATA));
	}

	@Test
	void ParkingAnalizerService_NoRedisData_Null() {
		var carData = new CarData(CAR_ID_NO_DATA, PARKING_ID);
		var serviceResponce = service.checkIfCarTimeOnParkingExpired(carData);
		assertNull(serviceResponce);
	}

	@Test
	void ParkingAnalizerService_ParkingTimeNotExpired_Null() {
		var carData = new CarData(CAR_ID_PARKING_TIME_NOT_EXPIRED, PARKING_ID);
		var serviceResponce = service.checkIfCarTimeOnParkingExpired(carData);
		assertNull(serviceResponce);
	}

	@Test
	void ParkingAnalizerService_ParkingTimeExpired_CarParkingTimeExpired() {
		var carData = new CarData(CAR_ID_PARKING_TIME_EXPIRED, PARKING_ID);
		var serviceResponce = service.checkIfCarTimeOnParkingExpired(carData);
		assertEquals(carData, serviceResponce);
	}
}
