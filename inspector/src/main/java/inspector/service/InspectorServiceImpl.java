package inspector.service;

import org.parking.model.CarData;
import org.parking.model.EnumStatus;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class InspectorServiceImpl implements InspectorService {

	@Override
	public EnumStatus isPangoPayed(CarData car) {
		// TODO Auto-generated method stub
		EnumStatus status = EnumStatus.unpaid;// сделать 85% вероятности в пангогенераторе
		return status;
	}

}
