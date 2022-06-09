package it.itsuptoyou.serviceimpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.itsuptoyou.collections.CarbonFootPrintMeasurement;
import it.itsuptoyou.collections.DTO.AverageValue;
import it.itsuptoyou.collections.DTO.CarbonFootPrintDTO;
import it.itsuptoyou.exceptions.PreconditionFailedException;
import it.itsuptoyou.repositories.CarbonFootprintRepository;
import it.itsuptoyou.service.CarbonFootPrintMeasureService;
import it.itsuptoyou.utils.CarbonFootPrintsUtils;

@Service
public class CarbonFootPrintMeasureServiceImpl implements CarbonFootPrintMeasureService{

	@Autowired
	private CarbonFootprintRepository carbonFootprintRepository;
	
	@Autowired
	private CarbonFootPrintsUtils carbonFootPrintsUtils;
	
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
				!(userMeasurement.getLastMeasurement()!=null && 
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
			userMeasurement.setNumberOfSamples(userMeasurement.getNumberOfSamples()+1);
		}else {
			//measurement overwrite
			if(userMeasurement.getNumberOfSamples()==0) {
				userMeasurement.getMeasurements().put(
						userMeasurement.getNumberOfSamples(),
						requestDTO);
			}else {
				userMeasurement.getMeasurements().put(
						userMeasurement.getNumberOfSamples()-1,
						requestDTO);
			}
			
		}
		
		userMeasurement.setComplete(carbonFootPrintsUtils.isLastMeasurementComplete(requestDTO));
		if(userMeasurement.isComplete()) {
			// if complete compute new averages
			List<AverageValue> newAverages = carbonFootPrintsUtils.computeTheAverages(userMeasurement);
			userMeasurement.setMeasurementAverages(newAverages);
			userMeasurement.setLastMeasurement(now);
		}
		userMeasurement.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
		userMeasurement= carbonFootprintRepository.save(userMeasurement);
		
		return userMeasurement;
	}
	
	
}
