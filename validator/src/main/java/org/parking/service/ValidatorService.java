package org.parking.service;

import org.parking.model.CarData;

public interface ValidatorService {
	CarData checkIfCarFined(CarData carData);
}
