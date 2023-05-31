package org.parking;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parking.model.CarData;
import org.parking.service.PakahBackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
class PakahBackOfficeControllerIntegrationTest {

	private static final String URN = "/pakah";
	private static final long CAR_ID = 10000001;
	private static final long PARKING_LOT_ID = 1;
	private static final long INCORRECT_CAR_ID = 1;
	private static CarData carData = new CarData(CAR_ID, PARKING_LOT_ID);
	@MockBean
	PakahBackOfficeService service;

	ObjectMapper mapper;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void mockingService() {
		when(service.sendDataToDataHandler(CAR_ID, PARKING_LOT_ID)).thenReturn(carData);
	}

	@Test
	void correctData() throws Exception {
		mapper = new ObjectMapper();
		byte[] content = mockMvc
				.perform(put(URN + "/{id}/{parkingId}", CAR_ID + "", PARKING_LOT_ID + ""))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsByteArray();
		var res = mapper.readValue(content, new TypeReference<CarData>(){});
		assertEquals(carData, res);
	}
	
//	@Test
	void incorrectCarId() throws Exception {
		mapper = new ObjectMapper();
		mockMvc.perform(put(URN + "/{id}/{parkingId}", INCORRECT_CAR_ID + "", PARKING_LOT_ID + ""))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	
}