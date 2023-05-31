package org.parking;

import java.io.IOException;
import org.junit.jupiter.api.*;
import org.parking.model.CarData;
import org.parking.service.PakahBackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class PakahBackOfficeServiceTests {

	private static final long CAR_ID = 10000001;

	private static final long PARKING_LOT_ID = 1;

	CarData carData = new CarData(CAR_ID, PARKING_LOT_ID);

	@Value("${app.back-office.pakahDataHandler.url}")
	public String url;
	@Value("${app.back-office.pakahDataHandler.urn}")
	public String urn;

	@MockBean
	WebClient webClientMock;
	
	@Autowired
	PakahBackOfficeService service;

	public static MockWebServer mockBackEnd;

	@BeforeAll
	static void setUp() throws IOException {
		mockBackEnd = new MockWebServer();
		mockBackEnd.start();
	}

	@BeforeEach
	void initialize() {
		String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
		service.createWebClientForTesting(baseUrl);
	}

	@Test
	void sendCorrectData_returnCarDataSameToSentData() throws Exception {
		ObjectMapper mapper = new ObjectMapper ();
	    CarData mockCarData = new CarData(10000001, 1);
	    mockBackEnd.enqueue(new MockResponse()
	      .setBody(mapper.writeValueAsString(mockCarData))
	      .addHeader("Content-Type", "application/json"));

	    CarData returnedCarData = service.sendDataToDataHandler(mockCarData.carID, mockCarData.parkingID);
	    var monoCarData = Mono.just(returnedCarData);

	    StepVerifier.create(monoCarData)
	      .expectNextMatches(carData -> carData.equals(mockCarData))
	      .verifyComplete();
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockBackEnd.shutdown();
	}
}