package org.parking;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.parking.service.ParkingAnalizerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class ParkingAnalyzerControllerTest {
	
	/**
	 * not completed yet
	 */

	@MockBean
	ParkingAnalizerServiceImpl service;
	@Autowired
	InputDestination producer;
	@Autowired
	OutputDestination consumer;
	
	@Value("${app.jumps.binding.name.producer:analyzerConsumer-in-0}")
	String bindingNameProducer;
	@Value("${app.jumps.binding.name.consumer:carAnalyzer-out-0}") // from my appl
	String bindingNameConsumer;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	void test() {
		assertNull(null);
	}

}
