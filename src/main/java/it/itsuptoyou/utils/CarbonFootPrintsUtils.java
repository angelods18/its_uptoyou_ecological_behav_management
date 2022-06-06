package it.itsuptoyou.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public List<AverageValue> computeTheAverages(CarbonFootPrintMeasurement request) {
		// request is the measurement with the last one already added and numberOfSamples already incremented
		log.info("calculate the sum");
		List<AverageValue> response = new ArrayList<>();
		List<AverageValue> averages= request.getMeasurementAverages();
		CarbonFootPrintDTO lastMeasurement = request.getMeasurements().get(request.getNumberOfSamples()-1);
		int oldSamples = request.getNumberOfSamples()-1;
		for (AverageValue averageValue : averages) {
			String measType = averageValue.getName();
			if(!measType.equals(TOTAL)) {
				Map<Integer,Double> meas = lastMeasurement.getGroupByName(measType);
				double oldAverage = averageValue.getValue()*oldSamples;
				//TODO average computing criteria: per field or per group?
				// first: per group --> sum of all fields
				averageValue.setValue((oldAverage+getSumPerGroup(meas))/request.getNumberOfSamples());
				response.add(averageValue);
			}
		}
		//update total average
		AverageValue totalAverage = averages.stream().filter(a -> a.getName().equals(TOTAL)).findAny().get();
		totalAverage.setValue(
				(totalAverage.getValue()*oldSamples + getSumOfAllGroup(averages))/request.getNumberOfSamples());
		response.add(totalAverage);
		
		return response;
	}
	
	public boolean isLastMeasurementComplete(CarbonFootPrintDTO request) {
		return (request.getAlimentation().size() == ALIMENTATION_FIELD &&
				request.getHouse().size() == HOUSE_FIELD &&
				request.getTransport().size() == TRANSPORT_FIELD);
	}
	
	public double getSumPerGroup(Map<Integer,Double> field) {
		double sum = 0.0;
		Set<Integer> entries = field.keySet();
		for (Integer integer : entries) {
			sum=sum+field.get(integer);
		}
		return sum;
	}
	
	public double getSumOfAllGroup(List<AverageValue> averages) {
		double sum = 0.0;
		sum=sum+averages.stream().filter(a -> a.getName().equals(ALIMENTATION)).findFirst().get().getValue();
		sum=sum+averages.stream().filter(a -> a.getName().equals(HOUSE)).findFirst().get().getValue();
		sum=sum+averages.stream().filter(a -> a.getName().equals(TRANSPORT)).findFirst().get().getValue();
		return sum;
	}
}
