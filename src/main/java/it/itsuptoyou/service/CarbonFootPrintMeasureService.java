package it.itsuptoyou.service;

import java.util.Map;

import it.itsuptoyou.collections.CarbonFootPrintMeasurement;
import it.itsuptoyou.collections.DTO.CarbonFootPrintDTO;
import it.itsuptoyou.exceptions.PreconditionFailedException;

public interface CarbonFootPrintMeasureService {

	CarbonFootPrintMeasurement registerMeasurement(String username,CarbonFootPrintDTO requestDTO) throws PreconditionFailedException;
	
	CarbonFootPrintMeasurement getUserMeasurement(String username);
	
	Map<String,Object> saveCommentToMeasurement(String username, Map<String,Object> requestBody);
}
