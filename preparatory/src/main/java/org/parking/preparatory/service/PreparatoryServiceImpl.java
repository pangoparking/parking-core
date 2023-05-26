package org.parking.preparatory.service;

import java.time.Instant;
import org.parking.model.EnumStatus;
import org.parking.model.Fine;
import org.parking.model.RawSqlData;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class PreparatoryServiceImpl implements FinePreparatoryService {

	@Override
	public Fine produceFine(RawSqlData rawSqlData) {
		log.trace("PreparatoryServiceImpl : produceFine : rawSqlData={}", rawSqlData);
		Fine fine = null;
		try {
			fine = createFine(rawSqlData);
			log.trace("createFine : fine created successfuly");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fine;
	}

	private Fine createFine(RawSqlData rawSqlData) {
		log.trace("PreparatoryServiceImpl : createFine");
		long pseudoId = Long.valueOf(String.format("%d%d", Instant.now().getEpochSecond(), rawSqlData.carID)); // crutch
		log.trace("createFine : pseudoId={}", pseudoId);
		Fine fine = new Fine(pseudoId, rawSqlData.ownerID, rawSqlData.ownerName, rawSqlData.ownerEmail,
				rawSqlData.carID, rawSqlData.parkingPlace, rawSqlData.fineBeenIssuedTime, rawSqlData.fineCost,
				EnumStatus.unpaid);
		log.trace("createFine : fine={}", fine);
		return fine;
	}

}
