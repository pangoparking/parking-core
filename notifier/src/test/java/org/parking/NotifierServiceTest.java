package org.parking;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.parking.model.EnumStatus;
import org.parking.model.Fine;
import org.parking.service.NotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.MessagingException;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class NotifierServiceTest {
	
	public Fine fine = new Fine(123,"Robert Peterson","rob@gmail.com",1234,null,null,100,EnumStatus.unpaid);

	
//	@MockBean
//	Preparatory preparatoryMicro;
	@Autowired
	NotifierService service;
	
	@RegisterExtension
	static GreenMailExtension mailExtension =
	new GreenMailExtension(ServerSetupTest.SMTP)
	.withConfiguration(GreenMailConfiguration.aConfig().withUser("parking", "12345.com"));
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	void test() throws MessagingException {
	//	when(dataProvider.getData(PATIENT_ID))
	//	.thenReturn(new NotificationData(DOCTOR_EMAIL, DOCTOR_NAME, PATIENT_NAME));
		
		//preparatoryMicro.send(new GenericMessage<PulseJump>(pulseJump),"jumpsConsumer-in-0");
	//	MimeMessage message = mailExtension.getReceivedMessages()[0];
	//	assertEquals(DOCTOR_EMAIL, message.getAllRecipients()[0].toString());
	//	assertTrue(message.getSubject().contains(PATIENT_NAME));
	}

	
	

}
