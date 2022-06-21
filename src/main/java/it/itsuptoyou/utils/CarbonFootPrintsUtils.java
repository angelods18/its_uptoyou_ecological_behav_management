package it.itsuptoyou.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.itsuptoyou.collections.CarbonFootPrintMeasurement;
import it.itsuptoyou.collections.DTO.AverageValue;
import it.itsuptoyou.collections.DTO.CarbonFootPrintDTO;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CarbonFootPrintsUtils {

	public static final int ALIMENTATION_FIELD = 10;
	public static final int HOUSE_FIELD = 3;
	public static final int TRANSPORT_FIELD = 10;
	
	public static final String TOTAL = "total";
	public static final String TRANSPORT = "transport";
	public static final String HOUSE = "house";
	public static final String ALIMENTATION = "alimentation";
	
	public static final String COEFFICIENT_FILE = "carbonfootprint.json";
	
	private static MeasureCoefficient measureCoefficient;
	
	@SuppressWarnings("unused")
	public List<AverageValue> computeTheAverages(CarbonFootPrintMeasurement request) {
		// request is the measurement with the last one already added and numberOfSamples already incremented
		log.info("calculate the sum");
		List<AverageValue> response = new ArrayList<>();
		List<AverageValue> averages= request.getMeasurementAverages();
		if(averages==null) {
			// this is the first time a compute the average
			CarbonFootPrintDTO meas = request.getMeasurements().get(0);
			Map<Integer,Double> alimentationMeas = meas.getAlimentation();
			Map<Integer,Double> houseMeas = meas.getHouse();
			Map<Integer,Double> transportMeas = meas.getTransport();
			AverageValue alimentationAverage = new AverageValue(); alimentationAverage.setName(ALIMENTATION);
			AverageValue houseAverage = new AverageValue(); houseAverage.setName(HOUSE);
			AverageValue transportAverage = new AverageValue(); transportAverage.setName(TRANSPORT);
			AverageValue totalAverage = new AverageValue(); totalAverage.setName(TOTAL);
			alimentationAverage.setValue(getSumPerGroup(alimentationMeas));
			houseAverage.setValue(getSumPerGroup(houseMeas));
			transportAverage.setValue(getSumPerGroup(transportMeas));
			totalAverage.setValue(alimentationAverage.getValue()+houseAverage.getValue()+transportAverage.getValue());
			response = Arrays.asList(totalAverage, alimentationAverage, houseAverage, transportAverage);
		}else {
			CarbonFootPrintDTO lastMeasurement = request.getMeasurements().get(request.getNumberOfSamples()-1);
			int oldSamplesIndex = request.getNumberOfSamples()-1;
			for (AverageValue averageValue : averages) {
				String measType = averageValue.getName();
				if(!measType.equals(TOTAL)) {
					Map<Integer,Double> meas = lastMeasurement.getGroupByName(measType);
					double oldAverage = averageValue.getValue()*oldSamplesIndex;
					//TODO average computing criteria: per field or per group?
					// first: per group --> sum of all fields
					averageValue.setValue((oldAverage+getSumPerGroup(meas))/request.getNumberOfSamples());
					response.add(averageValue);
				}
			}
			//update total average
			AverageValue totalAverage = averages.stream().filter(a -> a.getName().equals(TOTAL)).findAny().get();
			totalAverage.setValue(
					(totalAverage.getValue()*oldSamplesIndex + getSumOfAllGroup(averages))/request.getNumberOfSamples());
			response.add(totalAverage);
		}
		
		
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
	
	public CarbonFootPrintDTO computeCarbonFootPrintFromAnswers(CarbonFootPrintDTO request) {
		CarbonFootPrintDTO cfpDTO = new CarbonFootPrintDTO(true);
		request.getAlimentation().keySet().stream().forEach(p ->{
			cfpDTO.getAlimentation().put(p, request.getAlimentation().get(p) * getCoefficientByGroupAndPosition(ALIMENTATION, p.toString()));
		});
		request.getHouse().keySet().stream().forEach(p ->{
			cfpDTO.getHouse().put(p, request.getHouse().get(p) * getCoefficientByGroupAndPosition(HOUSE, p.toString()));
		});
		request.getTransport().keySet().stream().forEach(p ->{
			cfpDTO.getTransport().put(p, request.getTransport().get(p) * getCoefficientByGroupAndPosition(TRANSPORT, p.toString()));
		});
		return cfpDTO;
	}
	
	public Double getCoefficientByGroupAndPosition(String group, String position)  {
		if(measureCoefficient==null) {
			File cfpJson;
			try {
				cfpJson = new ClassPathResource(COEFFICIENT_FILE).getFile();
				ObjectMapper mapper = new ObjectMapper();
				measureCoefficient = mapper.readValue(cfpJson, MeasureCoefficient.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Errore in lettura file carbonfootprint.json " + e);
			} 
		}
		
		Map<String,Double> coefficientGroup=new HashMap<>();
		switch (group) {
		case ALIMENTATION:
			coefficientGroup=measureCoefficient.getAlimentation();
			break;
		case HOUSE:
			coefficientGroup=measureCoefficient.getHouse();
			break;
		case TRANSPORT:
			coefficientGroup=measureCoefficient.getTransport();
		default:
			break;
		}
		double	coefficient = coefficientGroup.get( String.valueOf(Integer.valueOf(position)+1));
		
		return coefficient;
	}
	
	@Data
	public static class MeasureCoefficient {
		private Map<String,Double> alimentation;
		private Map<String,Double> house;
		private Map<String,Double> transport;
	}
}
