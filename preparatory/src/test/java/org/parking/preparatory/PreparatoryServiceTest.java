package org.parking.preparatory;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.*;
import org.parking.model.CarData;
import org.parking.model.RawSqlData;
import org.parking.preparatory.service.DataRequestService;
import org.parking.preparatory.service.FinePreparatoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class PreparatoryServiceTest {

	@Autowired
	DataRequestService dataRequestService;
	@Autowired
	FinePreparatoryService finePreparatoryService;

	MockMvc mockMvc;
	
	CarData correctCarData = new CarData (10000000, 101);
	CarData wrongCarIdCarData = new CarData (1, 1);
	CarData wrongParkingLotNumCarData = new CarData (10000001, 10);
	
	@Value("${app.preparatory.CODP.url:http://localhost:8400}")
	private String url;
	@Value("${app.preparatory.CODP.urn:/parking/getCarInfo}")
	private String urn;
	
	RawSqlData rawSqlData;
	
//	@BeforeEach
	void setUpBeforeClass() throws Exception {
		var a = mockMvc.perform(get("/parking/getCarInfo", correctCarData)).andExpect(status().is(200)).andReturn();
		System.out.println(a);
	}

	// testing DataRequestService
	@Order(1)
	@Test
	void getDataFromCODP_correctData () {
		rawSqlData = dataRequestService.getDataFromCODP(correctCarData);
		if (rawSqlData != null) {
			System.out.println(rawSqlData);
			produceFine_correctData(rawSqlData);
		} else {
			System.out.println("not yet :(");
		}
	}
	
	void produceFine_correctData (RawSqlData rawSqlData) {
		var res = finePreparatoryService.produceFine(rawSqlData);
		if (res != null) {
			System.out.println(res);
		} else {
			System.out.println("not yet :(");
		}
		
	}
	
//	@Test
	void test() {
		log.trace("test");
		assertNull(null);
	}
}
