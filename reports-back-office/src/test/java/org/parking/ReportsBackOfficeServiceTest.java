package org.parking;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.parking.repository.FinesRepository;
import org.parking.service.ReportsBackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.parking.model.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReportsBackOfficeServiceTest {
	
	LocalDateTime date1 = LocalDateTime. of(2022, 1, 1, 12, 0);
	LocalDateTime date2 = LocalDateTime.of(2023, 1, 1, 12, 00);
	LocalDateTime date3 = LocalDateTime.of(2022, 2, 1, 12, 0);
	LocalDateTime date4 = LocalDateTime.of(2022, 2, 18, 12, 0);
	LocalDateTime date5 = LocalDateTime.of(2022, 3, 1, 12, 0);
	LocalDateTime date6 = LocalDateTime.of(2022, 3, 20, 12, 0);
	LocalDateTime date7 = LocalDateTime.of(2022, 3, 25, 12, 0);
	LocalDateTime date8 = LocalDateTime.of(2022, 11, 1, 12, 0);
	LocalDateTime date9 = LocalDateTime.of(2022, 11, 15, 12, 0);
	LocalDateTime date10 = LocalDateTime.of(2023, 2, 1, 12, 0);
	FineDoc[] docs = {
			FineDoc.of(new Fine(1, 1, "Owner1", "mail@mail.com", 1, "Parking1", date1, 150, EnumStatus.unpaid)),
			FineDoc.of(new Fine(2, 1, "Owner1", "mail@mail.com", 2, "Parking2", date2, 150, EnumStatus.unpaid)),
			FineDoc.of(new Fine(3, 1, "Owner1", "mail@mail.com", 3, "Parking3", date3, 150, EnumStatus.unpaid)),
			FineDoc.of(new Fine(4, 2, "Owner2", "mail@mail.com", 4, "Parking4", date4, 150, EnumStatus.unpaid)),
			FineDoc.of(new Fine(5, 2, "Owner2", "mail@mail.com", 4, "Parking5", date5, 150, EnumStatus.paid)),
			FineDoc.of(new Fine(6, 2, "Owner2", "mail@mail.com", 4, "Parking6", date6, 150, EnumStatus.paid)),
			FineDoc.of(new Fine(7, 2, "Owner2", "mail@mail.com", 4, "Parking7", date7, 150, EnumStatus.paid)),
			FineDoc.of(new Fine(8, 3, "Owner3", "mail@mail.com", 5, "Parking8", date8, 150, EnumStatus.canceled)),
			FineDoc.of(new Fine(9, 3, "Owner3", "mail@mail.com", 5, "Parking9", date9, 150, EnumStatus.canceled)),
			FineDoc.of(new Fine(10, 3, "Owner3", "mail@mail.com", 5, "Parking10", date10, 150, EnumStatus.canceled))
	};
	
	@Autowired
	ReportsBackOfficeService service;
	
	@Autowired
	FinesRepository repository;
	
	@Test
	@Order(1)
	void saveAndGetFinesByStatusALLTest() {
		for (int i = 0; i < docs.length; i++) {
			repository.save(docs[i]);
		}
		assertIterableEquals(List.of(docs), service.getFinesByStatus("all"));	
	}
	
	@Test
	@Order(2)
	void getFinesByStatusALLAndDateTimeIntervalTest() {
		assertIterableEquals(List.of(docs[1], docs[9]), service.getFinesByStatusAndDateTimeInterval("all", date2, date10));
	}
	
	@Test
	@Order(3)
	void getFinesByStatusUNPAIDTest() {
		assertIterableEquals(List.of(docs[0], docs[1], docs[2], docs[3]), service.getFinesByStatus("unpaid"));
	}
	
	@Test
	@Order(4)
	void getFinesByStatusUNPAIDAndDateTimeIntervalTest() {
		assertIterableEquals(List.of(docs[1], docs[3]), service.getFinesByStatusAndDateTimeInterval("unpaid", date4, date2));
	}
	
	@Test
	@Order(5)
	void getFinesByStatusPAIDTest() {
		assertIterableEquals(List.of(docs[4], docs[5], docs[6]), service.getFinesByStatus("paid"));
	}
	
	@Test
	@Order(6)
	void getFinesByStatusPAIDAndDateTimeIntervalTest() {
		assertIterableEquals(List.of(docs[5], docs[6]), service.getFinesByStatusAndDateTimeInterval("paid", date6, date7));
	}
	
	@Test
	@Order(7)
	void getFinesByStatusCANCELEDTest() {
		assertIterableEquals(List.of(docs[7], docs[8], docs[9]), service.getFinesByStatus("canceled"));
	}
	
	@Test
	@Order(8)
	void getFinesByStatusCANCELEDAndDateTimeIntervalTest() {
		assertIterableEquals(List.of(docs[7], docs[8]), service.getFinesByStatusAndDateTimeInterval("canceled", date8, date9));
	}
	
	@Test
	@Order(9)
	void getFinesByKeyAndIDTest() {
		assertIterableEquals(List.of(docs[0], docs[1], docs[2]), service.getFinesByKeyAndID("ownerID", 1));
		assertIterableEquals(List.of(docs[0]), service.getFinesByKeyAndID("carID", 1));
	}
	
	@Test
	@Order(10)
	void getFinesByKeyAndIDAndDateTimeIntervalTest() {
		assertIterableEquals(List.of(docs[1], docs[2]), service.getFinesByKeyAndIDAndDateTimeInterval("ownerID", 1, date3, date2));
		assertIterableEquals(List.of(docs[3], docs[4], docs[5]), service.getFinesByKeyAndIDAndDateTimeInterval("carID", 4, date4, date6));
	}
	
	@Test
	@Order(11)
	void getFinesByKeyAndIDExceptionTest() {
		assertThrows(NoSuchElementException.class, () -> service.getFinesByKeyAndID("ownerID", 10));
		assertThrows(NoSuchElementException.class, () -> service.getFinesByKeyAndID("carID", 10));
	}
	
	@Test
	@Order(12)
	void getFinesByYearTest() {
		assertEquals(Map.of(1, 1, 2, 1), service.getFinesByYear(2023));
		assertEquals(Map.of(1, 1, 2, 2, 3, 3, 11, 2), service.getFinesByYear(2022));
		assertEquals(Map.of(), service.getFinesByYear(2030));
	}

	@Test
	@Order(13)
	void getFineByIdTest() {
		assertEquals(docs[0], service.getFineByID(1));
		assertEquals(docs[9], service.getFineByID(10));
	}
	
	@Test
	@Order(14)
	void getFineByIdExceptionTest() {
		assertThrows(NoSuchElementException.class, () -> service.getFineByID(111));
	}
	
	@Test
	@Order(15)
	void updateFineStatusTest() {
		FineDoc doc = docs[0];
		assertEquals("unpaid", doc.getStatus());
		doc.setStatus("paid");
		assertEquals(doc, service.updateFineStatus(1, EnumStatus.paid));
	}
	
	@Test
	@Order(16)
	void updateFineStatusExceptionTest() {
		assertThrows(NoSuchElementException.class, () -> service.updateFineStatus(111, EnumStatus.paid));
	}

}