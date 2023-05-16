package org.parking.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public class StatusUpdateDto {
	
	private static final String VALID_ID_MSG = "id can't be null or less than 1";
	private static final String REGEXP_STATUS = "paid||unpaid||canceled";
	private static final String REGEXP_STATUS_MSG = "status should be one of: paid, unpaid, canceled";
	
	@Min(value = 1, message = VALID_ID_MSG)
	public long id;
	
	@Pattern(regexp = REGEXP_STATUS, message = REGEXP_STATUS_MSG)
	public String status;
	
}