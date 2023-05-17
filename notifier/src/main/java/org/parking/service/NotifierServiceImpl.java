package org.parking.service;

import org.parking.model.Fine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class NotifierServiceImpl implements NotifierService {

	@Value("${spring.mail.username}")
	public String emailFrom;
	
	@Value("${app.notifier.email.to}")
	public String emailTo;
	
	
    @Autowired
    private JavaMailSender javaMailSender;
	
	@Override
	public String SendMailService(Fine fine) {
	
		return (fine.email!=null)? sendSimpleMail(fine):null;
	}
	
	public String sendSimpleMail(Fine fine) {

		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			//mailMessage.setFrom(emailFrom);
			mailMessage.setTo(fine.email);
			mailMessage.setText(String.format( "Car fined carId:%d",fine.carID));
			mailMessage.setSubject("Parking information");

			javaMailSender.send(mailMessage);
			log.trace("Email sent to %s",emailTo);
			return "Mail Sent Successfully...";
		}

		catch (Exception e) {
			return "Error while Sending Mail";
		}
	}


}
