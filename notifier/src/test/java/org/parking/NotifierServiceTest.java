package org.parking;

import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.parking.model.EnumStatus;
import org.parking.model.Fine;
import org.parking.service.NotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class NotifierServiceTest {
	@Autowired
	InputDestination producer;
	
	
    private static final String BINDING_NAME = "notifierConsumer-in-0";
 
	public Fine fine = new Fine(123,"Robert Peterson","katebox85@gmail.com",1234,null,null,100,EnumStatus.unpaid);
//	public Fine fineNullEmail = new Fine(123,"Robert Peterson",null,1234,null,null,100,EnumStatus.unpaid);

	@RegisterExtension
	static public GreenMailExtension mailExtension = new GreenMailExtension(ServerSetupTest.SMTP).
	withConfiguration(GreenMailConfiguration.aConfig().withUser("telran46", "12345"));
	
	@BeforeAll
	static void setUpBeforeAll() throws Exception{};
	
	@Test
	void test() {
		assertEquals(1,1);
	}

	@Test
    void notifierMailSendTest() throws MessagingException {
		producer.send(new GenericMessage<Fine>(fine),BINDING_NAME);
		MimeMessage message =mailExtension.getReceivedMessages()[0];
		assertEquals(fine.email,message.getAllRecipients()[0].toString());
     	//assertTrue(message.getSubject().contains(fine.carID));
		
	}
	

}
