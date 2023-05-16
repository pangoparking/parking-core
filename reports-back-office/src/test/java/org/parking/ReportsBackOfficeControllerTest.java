package org.parking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.parking.model.EnumStatus;
import org.parking.model.Fine;
import org.parking.model.StatusUpdateDto;
import org.parking.service.ReportsBackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ReportsBackOfficeControllerTest {
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	ReportsBackOfficeService service;
	
	@Nested
	@DisplayName("get fine by ID")
	class FineById {
		
		private static final FineDoc DOC = FineDoc.of(
				new Fine(1, 1, "Owner1", "mail@mail.com", 1, "Parking1", LocalDateTime. of(2022, 1, 1, 12, 0), 150, EnumStatus.unpaid));
		
		@BeforeEach
		void mocking() {
			when(service.getFineByID(ArgumentMatchers.anyLong())).thenReturn(DOC);
		}
		
		@Test
		@DisplayName("get fine by ID")
		void getAllFines() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/id/1")).andDo(print())
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			FineDoc res = mapper.readValue(content, FineDoc.class);
			assertEquals(DOC, res);
		}
	}
	
	@Nested
	@DisplayName("get fines")
	class AllFines {
		
		private static final String ALL = "all";
		private static final List<FineDoc> LIST_DOCS = List.of(FineDoc.of(
				new Fine(1, 1, "Owner1", "mail@mail.com", 1, "Parking1", LocalDateTime. of(2022, 1, 1, 12, 0), 150, EnumStatus.unpaid)));
		private static final List<FineDoc> LIST_DOCS_BY_DATES = List.of(FineDoc.of(
				new Fine(6, 2, "Owner2", "mail@mail.com", 4, "Parking6", LocalDateTime.of(2022, 3, 20, 12, 0), 150, EnumStatus.paid)));
		
		@BeforeEach
		void mocking() {
			when(service.getFinesByStatus(ALL)).thenReturn(LIST_DOCS);
			when(service.getFinesByStatusAndDateTimeInterval(
					ArgumentMatchers.matches(ALL),
					ArgumentMatchers.any(LocalDateTime.class),
					ArgumentMatchers.any(LocalDateTime.class)))
			.thenReturn(LIST_DOCS_BY_DATES);			
		}
		
		@Test
		@DisplayName("get all fines")
		void getAllFines() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/all")).andDo(print())
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference <List<FineDoc>>() {});
			assertEquals(LIST_DOCS, res);
		}
		
		@Test
		@DisplayName("get all fines from date to date")
		void getAllFinesByDatesInteval() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/all")
					.param("from", "2022-01-01T12:00")
					.param("to", "2023-01-01T12:00"))
					.andDo(print())
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference <List<FineDoc>>() {});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get all fines from date")
		void getAllFinesFromDate() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/all")
					.param("from", "2022-01-01T12:00")).andDo(print()).andExpect(status().isOk()).andReturn()
					.getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>() {});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get all fines to date")
		void getAllFinesToDate() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/all")
					.param("to", "2022-01-01T12:00")).andDo(print()).andExpect(status().isOk()).andReturn()
					.getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>() {});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get all fines dates on the contrary (IllegalStateException)")
		void getAllFinesDatesContrary() throws Exception {
			String expected = "date 'from' 2023-01-01T12:00 can't be after date 'to' 2022-01-01T12:00";
			String content = mockMvc.perform(get("/fines/all")
					.param("from", "2023-01-01T12:00")
					.param("to", "2022-01-01T12:00"))
					.andDo(print()).andExpect(status().isBadRequest()).andReturn()
					.getResponse().getContentAsString();
			assertEquals(expected, content);
		}
	}

	@Nested
	@DisplayName("get fines by status PAID")
	class FinesByStatusPaid {
		
		private static final String PAID = EnumStatus.paid.name();
		private static final List<FineDoc> LIST_DOCS = List.of(FineDoc.of(
				new Fine(5, 2, "Owner2", null, 4, "Parking5", LocalDateTime.of(2022, 3, 1, 12, 0), 150, EnumStatus.paid)));
		private static final List<FineDoc> LIST_DOCS_BY_DATES = List.of(FineDoc.of(
				new Fine(6, 2, "Owner2", null, 4, "Parking6", LocalDateTime.of(2022, 3, 20, 12, 0), 150, EnumStatus.paid)));

		@BeforeEach
		void mocking() {
			when(service.getFinesByStatus(PAID)).thenReturn(LIST_DOCS);
			when(service.getFinesByStatusAndDateTimeInterval(
					ArgumentMatchers.matches(PAID),
					ArgumentMatchers.any(LocalDateTime.class),
					ArgumentMatchers.any(LocalDateTime.class)))
			.thenReturn(LIST_DOCS_BY_DATES);
		}
		
		@Test
		@DisplayName("get fines by status PAID all dates")
		void getFinesByStatusTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/paid")).andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS, res);
		}
		
		@Test
		@DisplayName("get fines by status PAID from date to date")
		void getFinesByStatusAndDatesTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/paid")
					.param("from", "2023-01-01T12:00")
					.param("to", "2023-01-02T12:00"))
					.andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get fines by status PAID from date")
		void getFinesByStatusAndFromDateTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/paid")
					.param("from", "2023-01-01T12:00"))
					.andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get fines by status PAID to date")
		void getFinesByStatusAndToDateTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/paid")
					.param("to", "2023-01-01T12:00"))
					.andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
	}

	@Nested
	@DisplayName("get fines by status UNPAID")
	class FinesByStatusUnpaid {
		
		private static final String UNPAID = EnumStatus.unpaid.name();
		private static final List<FineDoc> LIST_DOCS = List.of(FineDoc.of(
				new Fine(1, 1, "Owner1", "mail@mail.com", 1, "Parking1", LocalDateTime. of(2022, 1, 1, 12, 0), 150, EnumStatus.unpaid)));
		private static final List<FineDoc> LIST_DOCS_BY_DATES = List.of(FineDoc.of(
				new Fine(2, 1, "Owner1", "mail@mail.com", 2, "Parking2", LocalDateTime.of(2023, 1, 1, 12, 00), 150, EnumStatus.unpaid)));

		@BeforeEach
		void mocking() {
			when(service.getFinesByStatus(UNPAID)).thenReturn(LIST_DOCS);
			when(service.getFinesByStatusAndDateTimeInterval(
					ArgumentMatchers.matches(UNPAID),
					ArgumentMatchers.any(LocalDateTime.class),
					ArgumentMatchers.any(LocalDateTime.class)))
			.thenReturn(LIST_DOCS_BY_DATES);
		}
		
		@Test
		@DisplayName("get fines by status UNPAID all dates")
		void getFinesByStatusTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/unpaid")).andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS, res);
		}
		
		@Test
		@DisplayName("get fines by status UNPAID from date to date")
		void getFinesByStatusAndDatesTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/unpaid")
					.param("from", "2023-01-01T12:00")
					.param("to", "2023-01-02T12:00"))
					.andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get fines by status UNPAID from date")
		void getFinesByStatusAndFromDateTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/unpaid")
					.param("from", "2023-01-01T12:00"))
					.andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get fines by status UNPAID to date")
		void getFinesByStatusAndToDateTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/unpaid")
					.param("to", "2023-01-01T12:00"))
					.andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
	}

	@Nested
	@DisplayName("get fines by status CANCELED")
	class FinesByStatusCanceled {
		
		private static final String CANCELED = EnumStatus.canceled.name();
		private static final List<FineDoc> LIST_DOCS = List.of(FineDoc.of(
				new Fine(8, 3, "Owner3", null, 5, "Parking8", LocalDateTime.of(2022, 11, 1, 12, 0), 150, EnumStatus.canceled)));
		private static final List<FineDoc> LIST_DOCS_BY_DATES = List.of(FineDoc.of(
				new Fine(9, 3, "Owner3", null, 5, "Parking9", LocalDateTime.of(2022, 11, 15, 12, 0), 150, EnumStatus.canceled)));

		@BeforeEach
		void mocking() {
			when(service.getFinesByStatus(CANCELED)).thenReturn(LIST_DOCS);
			when(service.getFinesByStatusAndDateTimeInterval(
					ArgumentMatchers.matches(CANCELED),
					ArgumentMatchers.any(LocalDateTime.class),
					ArgumentMatchers.any(LocalDateTime.class)))
			.thenReturn(LIST_DOCS_BY_DATES);
		}
		
		@Test
		@DisplayName("get fines by status CANCELED all dates")
		void getFinesByStatusTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/canceled")).andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS, res);
		}
		
		@Test
		@DisplayName("get fines by status CANCELED from date to date")
		void getFinesByStatusAndDatesTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/canceled")
					.param("from", "2023-01-01T12:00")
					.param("to", "2023-01-02T12:00"))
					.andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get fines by status CANCELED from date")
		void getFinesByStatusAndFromDateTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/canceled")
					.param("from", "2023-01-01T12:00"))
					.andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get fines by status CANCELED to date")
		void getFinesByStatusAndToDateTest() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/canceled")
					.param("to", "2023-01-01T12:00"))
					.andDo(print())
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>(){});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
	}
	
	@Nested
	@DisplayName("get fines by owner id")
	class FinesByOwnerId {
		
		private static final String OWNER_ID = "ownerID";
		private static final List<FineDoc> LIST_DOCS = List.of(FineDoc.of(
				new Fine(5, 2, "Owner2", "mail@mail.com", 4, "Parking5", LocalDateTime.of(2022, 3, 1, 12, 0), 150, EnumStatus.paid)));
		private static final List<FineDoc> LIST_DOCS_BY_DATES = List.of(FineDoc.of(
				new Fine(6, 2, "Owner2", "mail@mail.com", 4, "Parking6", LocalDateTime.of(2022, 3, 20, 12, 0), 150, EnumStatus.paid)));
	
		@BeforeEach
		void mocking() {
			when(service.getFinesByKeyAndID(
					ArgumentMatchers.matches(OWNER_ID),
					ArgumentMatchers.anyLong()))
			.thenReturn(LIST_DOCS);
			when(service.getFinesByKeyAndIDAndDateTimeInterval(
					ArgumentMatchers.matches(OWNER_ID),
					ArgumentMatchers.anyLong(),
					ArgumentMatchers.any(LocalDateTime.class),
					ArgumentMatchers.any(LocalDateTime.class)))
			.thenReturn(LIST_DOCS_BY_DATES);
		}
		
		@Test
		@DisplayName("get fines by owner id")
		void getFinesByOwnerId() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/ownerID/2")).andDo(print())
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>() {});
			assertEquals(LIST_DOCS, res);
		}
		
		@Test
		@DisplayName("get fines by owner id from date to date")
		void fetFinesByOwnerIdAndDates() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/ownerID/2")
					.param("from", "2023-01-01T12:00")
					.param("to", "2023-01-02T12:00"))
					.andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>() {});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get fines by owner id from date")
		void fetFinesByOwnerIdFromDates() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/ownerID/2")
					.param("from", "2023-01-01T12:00"))
					.andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>() {});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get fines by owner id to date")
		void fetFinesByOwnerIdToDates() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/ownerID/2")
					.param("to", "2023-01-01T12:00"))
					.andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>() {});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
	}
	
	@Nested
	@DisplayName("get fines by car id")
	class FinesByCarId {
		
		private static final String CAR_ID = "carID";
		private static final List<FineDoc> LIST_DOCS = List.of(FineDoc.of(
				new Fine(5, 2, "Owner2", "mail@mail.com", 4, "Parking5", LocalDateTime.of(2022, 3, 1, 12, 0), 150, EnumStatus.paid)));
		private static final List<FineDoc> LIST_DOCS_BY_DATES = List.of(FineDoc.of(
				new Fine(6, 2, "Owner2", "mail@mail.com", 4, "Parking6", LocalDateTime.of(2022, 3, 20, 12, 0), 150, EnumStatus.paid)));
	
		@BeforeEach
		void mocking() {
			when(service.getFinesByKeyAndID(
					ArgumentMatchers.matches(CAR_ID),
					ArgumentMatchers.anyLong()))
			.thenReturn(LIST_DOCS);
			when(service.getFinesByKeyAndIDAndDateTimeInterval(
					ArgumentMatchers.matches(CAR_ID),
					ArgumentMatchers.anyLong(),
					ArgumentMatchers.any(LocalDateTime.class),
					ArgumentMatchers.any(LocalDateTime.class)))
			.thenReturn(LIST_DOCS_BY_DATES);
		}
		
		@Test
		@DisplayName("get fines by car id")
		void getFinesByCarId() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/carID/4")).andDo(print())
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>() {});
			assertEquals(LIST_DOCS, res);
		}
		
		@Test
		@DisplayName("get fines by car id from date to date")
		void fetFinesByCarIdAndDates() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/carID/4")
					.param("from", "2023-01-01T12:00")
					.param("to", "2023-01-02T12:00"))
					.andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>() {});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get fines by car id from date")
		void fetFinesByCarIdFromDates() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/carID/4")
					.param("from", "2023-01-01T12:00"))
					.andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>() {});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
		
		@Test
		@DisplayName("get fines by car id to date")
		void fetFinesByCarIdToDates() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/carID/4")
					.param("to", "2023-01-01T12:00"))
					.andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			List<FineDoc> res = mapper.readValue(content, new TypeReference<List<FineDoc>>() {});
			assertEquals(LIST_DOCS_BY_DATES, res);
		}
	}
	
	@Nested
	@DisplayName("get fines by year")
	class FinesByYear{
		
		private static final Map<Integer, Integer> DOC = Map.of(1, 1, 2, 1);
		
		@BeforeEach
		void mocking() {
			when(service.getFinesByYear(ArgumentMatchers.anyInt())).thenReturn(DOC);
		}
		
		@Test
		@DisplayName("get fines by year")
		void updateStatusOfFine() throws Exception {
			byte[] content = mockMvc.perform(get("/fines/year/2023")).andDo(print())
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
			Map<Integer, Integer> res = mapper.readValue(content, new TypeReference<Map<Integer, Integer>>(){});
			assertEquals(DOC, res);
		}
	}
	
	@Nested
	@DisplayName("update status of fine")
	class UpdateStatusOfFine{
		
		private static final FineDoc DOC = FineDoc.of(
				new Fine(1, 1, "Owner1", "mail@mail.com", 1, "Parking1", LocalDateTime. of(2022, 1, 1, 12, 0), 150, EnumStatus.paid));
		
		@BeforeEach
		void mocking() {
			when(service.updateFineStatus(ArgumentMatchers.anyLong(),
					ArgumentMatchers.any(EnumStatus.class))).thenReturn(DOC);
		}
		
		@Test
		@DisplayName("update status of fine")
		void updateStatusOfFine() throws Exception {
			StatusUpdateDto dto = new StatusUpdateDto();
			dto.id = 1;
			dto.status = EnumStatus.paid.name();
			String dtoJson = mapper.writeValueAsString(dto);
			byte[] content = mockMvc.perform(put("/fines/update")
					.contentType(MediaType.APPLICATION_JSON).content(dtoJson))
					.andDo(print()).andExpect(status().isOk()).andReturn()
					.getResponse().getContentAsByteArray();
			FineDoc res = mapper.readValue(content, FineDoc.class);
			assertEquals(DOC, res);
		}
	}

}