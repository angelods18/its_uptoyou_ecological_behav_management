package it.itsuptoyou.utils;

import java.util.List;

import it.itsuptoyou.collections.CarbonFootPrintMeasurement;
import it.itsuptoyou.collections.DTO.AverageValue;
import it.itsuptoyou.collections.DTO.CarbonFootPrintDTO;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class CarbonFootPrintsUtils {

	public static final int ALIMENTATION_FIELD = 10;
	public static final int HOUSE_FIELD = 3;
	public static final int TRANSPORT_FIELD = 10;
	
	public static final String TOTAL = "total";
	public static final String TRANSPORT = "transport";
	public static final String HOUSE = "house";
	public static final String ALIMENTATION = "alimentation";
	
	@SuppressWarnings("unused")
	private List<AverageValue> computeTheAverages(CarbonFootPrintMeasurement request) {
		log.info("calculate the sum");
		List<AverageValue> averages= request.getMeasurementAverages();
		CarbonFootPrintDTO lastMeasurement = request.getMeasurements().get(request.getNumberOfSamples()-1);
		
		return null;
	}
	
	public boolean isLastMeasurementComplete(CarbonFootPrintDTO request) {
		return (request.getAlimentation().size() == ALIMENTATION_FIELD &&
				request.getHouse().size() == HOUSE_FIELD &&
				request.getTransport().size() == TRANSPORT_FIELD);
	}
}
