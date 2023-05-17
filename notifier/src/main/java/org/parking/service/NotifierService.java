package org.parking.service;

import org.parking.model.Fine;

public interface NotifierService {
   String SendMailService(Fine fine);
}
