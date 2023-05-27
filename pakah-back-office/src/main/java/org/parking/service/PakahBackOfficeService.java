package org.parking.service;

import org.parking.model.CarData;

public interface PakahBackOfficeService {
	CarData sendDataToDataHandler (long Id, long parkingId);
}
