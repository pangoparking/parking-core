package org.parking;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.parking.model.EnumStatus;
import org.parking.model.Fine;
import org.parking.repository.FinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class DbFinesPopulatorTest {

	private static final String BINDING_NAME = "fine";

	@Autowired
	InputDestination producer;
	
	@Autowired
	FinesRepository finesRepository;

	private Fine firstFine = new Fine(1, 1, "Alex", "mail@mail.com", 1, "parking 1", LocalDateTime.of(2023, 5, 5, 12, 0), 150, EnumStatus.unpaid);
	private Fine secondFine = new Fine(2, 2, "Victor", "mail@mail.com", 2, "parking 2", LocalDateTime.of(2023, 5, 5, 13, 0), 250, EnumStatus.unpaid);
	private FineDoc firstFineDoc = FineDoc.of(firstFine);
	private FineDoc secondFineDoc = FineDoc.of(secondFine);
	
	@Test
	void dbFinesPopulatorTest() {
		producer.send(new GenericMessage<Fine>(firstFine), BINDING_NAME);
		producer.send(new GenericMessage<Fine>(secondFine), BINDING_NAME);
		List<FineDoc> documents = finesRepository.findAll();
		List<FineDoc> expected = Arrays.asList(firstFineDoc, secondFineDoc);
		assertIterableEquals(expected, documents);
	}

}
