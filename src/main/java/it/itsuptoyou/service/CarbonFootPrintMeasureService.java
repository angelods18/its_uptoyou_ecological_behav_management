package it.itsuptoyou.service;

import it.itsuptoyou.collections.CarbonFootPrintMeasurement;
import it.itsuptoyou.collections.DTO.CarbonFootPrintDTO;
import it.itsuptoyou.exceptions.PreconditionFailedException;

public interface CarbonFootPrintMeasureService {

	CarbonFootPrintMeasurement registerMeasurement(String username,CarbonFootPrintDTO requestDTO) throws PreconditionFailedException;
}
