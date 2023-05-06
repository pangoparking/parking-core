package org.parking.service;

import org.parking.model.CarData;

public interface ParkingAnalizerService {
	CarData checkIfCarTimeOnParkingExpired(CarData carData);
}
