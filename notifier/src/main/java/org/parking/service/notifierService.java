package org.parking.service;

import org.parking.model.CarData;
import org.parking.model.Fine;

public interface notifierService {
   Fine SendMailService(Fine fine);
}
