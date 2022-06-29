package it.itsuptoyou.serviceimpl;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.itsuptoyou.collections.CarbonFootPrintMeasurement;
import it.itsuptoyou.collections.DTO.AverageValue;
import it.itsuptoyou.collections.DTO.CarbonFootPrintDTO;
import it.itsuptoyou.exceptions.PreconditionFailedException;
import it.itsuptoyou.repositories.CarbonFootprintRepository;
import it.itsuptoyou.service.CarbonFootPrintMeasureService;
import it.itsuptoyou.utils.CarbonFootPrintsUtils;
import it.itsuptoyou.utils.RestTemplateUtils;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CarbonFootPrintMeasureServiceImpl implements CarbonFootPrintMeasureService{

	private static final String GATEWAY_URL = "http://localhost:8022/";
	private static final String MANAGEMENT_SERVICE_URL = "management-service/";
	
	@Autowired
	private CarbonFootprintRepository carbonFootprintRepository;
	
	@Autowired
	private CarbonFootPrintsUtils carbonFootPrintsUtils;
	
	@Autowired
	private RestTemplateUtils restTemplateUtils;
	
	@Override
	public CarbonFootPrintMeasurement registerMeasurement(String username, CarbonFootPrintDTO requestDTO) throws PreconditionFailedException {
		//check if all the group are empty
		if(requestDTO.getAlimentation().isEmpty() && 
				requestDTO.getHouse().isEmpty() && 
				requestDTO.getTransport().isEmpty()) {
			throw new PreconditionFailedException("carbonFootPrint","emptyValue");
		}
		//find meas for user
		Optional<CarbonFootPrintMeasurement> userMeasurementsOpt = carbonFootprintRepository.findByUsername(username);
		CarbonFootPrintMeasurement userMeasurement;
		if(userMeasurementsOpt.isEmpty()) {
			//first object, field initialization
			userMeasurement = new CarbonFootPrintMeasurement();
			userMeasurement.setMeasurements(new HashMap<>());
			userMeasurement.setCreatedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
			userMeasurement.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
			userMeasurement.setUsername(username);
		}else {
			userMeasurement = userMeasurementsOpt.get();
		}
		LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
		// weekly check
		if( userMeasurement.getLastMeasurement()!= null &&
				!(//userMeasurement.getLastMeasurement()!=null && 
				now.minus(Duration.ofDays(7)).isAfter(userMeasurement.getLastMeasurement()) &&
				userMeasurement.isComplete())) 
		{
			//failed weekly check
			throw new PreconditionFailedException("carbonFootPrint","maxOnceAWeek");
		}
		// Insert measurement
		// Compute real values multiplying with coefficient
		requestDTO = carbonFootPrintsUtils.computeCarbonFootPrintFromAnswers(requestDTO);
		//check isComplete
		if(userMeasurement.isComplete()) {
			//create a new measure
			userMeasurement.getMeasurements().put(userMeasurement.getNumberOfSamples(), requestDTO);
		}else {
			//measurement overwrite
			userMeasurement.getMeasurements().put(
					userMeasurement.getNumberOfSamples(),
					requestDTO);			
		}
		
		userMeasurement.setComplete(carbonFootPrintsUtils.isLastMeasurementComplete(requestDTO));
		if(userMeasurement.isComplete()) {
			// if complete compute new averages
			userMeasurement.setNumberOfSamples(userMeasurement.getNumberOfSamples()+1);
			List<AverageValue> newAverages = carbonFootPrintsUtils.computeTheAverages(userMeasurement);
			List<Double> totalAverages = (userMeasurement.getTotalAverageValues()!=null) ?
					userMeasurement.getTotalAverageValues() : new ArrayList<>();
			totalAverages.add(newAverages.stream().filter(av -> av.getName().equals("total")).findFirst().get().getValue());
			userMeasurement.setTotalAverageValues(totalAverages);
			userMeasurement.setMeasurementAverages(newAverages);
			userMeasurement.setLastMeasurement(now);
		}
		userMeasurement.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		userMeasurement= carbonFootprintRepository.save(userMeasurement);
		
		return userMeasurement;
	}
	
	@Override
	public CarbonFootPrintMeasurement getUserMeasurement(String username) {
		// TODO Auto-generated method stub
		Optional<CarbonFootPrintMeasurement> userMeasurement = carbonFootprintRepository.findByUsername(username);
		
		if(userMeasurement.isEmpty()) {
			log.debug("no past measurements");
			CarbonFootPrintMeasurement emptyMeas = new CarbonFootPrintMeasurement();
			return emptyMeas;
		}else {
			// trasnform every measurement back to user answers
			CarbonFootPrintMeasurement measure = userMeasurement.get();
			Set<Integer> numberOfMeasure = measure.getMeasurements().keySet();
			for (Integer integer : numberOfMeasure) {
				measure.getMeasurements().put(integer, carbonFootPrintsUtils.computeAnswersFromCarbonFootPrint(measure.getMeasurements().get(integer)));
			}
			return measure;
		}
	}
	
	@Override
	public Map<String, Object> saveCommentToMeasurement(String username, Map<String, Object> requestBody) {
		// TODO Auto-generated method stub
		String token = requestBody.get("token").toString();
		restTemplateUtils.getRequest(GATEWAY_URL+MANAGEMENT_SERVICE_URL+"protected/profile", token, requestBody);
		
		
		return null;
	}
	
	
}
