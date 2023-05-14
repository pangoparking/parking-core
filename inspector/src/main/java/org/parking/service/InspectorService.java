package org.parking.service;

import org.parking.model.CarData;
import org.parking.model.EnumStatus;

public interface InspectorService {
	
EnumStatus isPangoPayed(CarData car);
}
