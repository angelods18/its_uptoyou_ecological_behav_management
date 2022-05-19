package it.itsuptoyou.serviceimpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.itsuptoyou.collections.CarbonFootPrintMeasurement;
import it.itsuptoyou.collections.DTO.CarbonFootPrintDTO;
import it.itsuptoyou.exceptions.PreconditionFailedException;
import it.itsuptoyou.repositories.CarbonFootprintRepository;
import it.itsuptoyou.service.CarbonFootPrintMeasureService;
import it.itsuptoyou.utils.CarbonFootPrintsUtils;

@Service
public class CarbonFootPrintMeasureServiceImpl implements CarbonFootPrintMeasureService{

	@Autowired
	private CarbonFootprintRepository carbonFootprintRepository;
	
	private CarbonFootPrintsUtils carbonFootPrintsUtils;
	
	@Override
	public CarbonFootPrintMeasurement registerMeasurement(String username, CarbonFootPrintDTO requestDTO) throws PreconditionFailedException {
		// TODO Auto-generated method stub
		
		if(requestDTO.getAlimentation().isEmpty() && 
				requestDTO.getHouse().isEmpty() && 
				requestDTO.getTransport().isEmpty()) {
			throw new PreconditionFailedException("carbonFootPrint","emptyValue");
		}
		
		Optional<CarbonFootPrintMeasurement> userMeasurementsOpt = carbonFootprintRepository.findByUsername(username);
		CarbonFootPrintMeasurement userMeasurement;
		if(userMeasurementsOpt.isEmpty()) {
			userMeasurement = new CarbonFootPrintMeasurement();
			userMeasurement.setCreatedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
			userMeasurement.setLastModifiedDate(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
			userMeasurement.setUsername(username);
		}else {
			userMeasurement = userMeasurementsOpt.get();
		}
		LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
		// controllo sulla settimana
		if( !(userMeasurement.getLastMeasurement()!=null && 
				now.minus(Duration.ofDays(7)).isAfter(userMeasurement.getLastMeasurement()) &&
				userMeasurement.isComplete())) 
		{
			//controllo temporale fallito
			throw new PreconditionFailedException("carbonFootPrint","maxOnceAWeek");
		}
		//Logica di inserimento misure e controllo che ci siano tutte 
		//check su isComplete
		if(userMeasurement.isComplete()) {
			//deve essere creata una nuova misura da zero
			userMeasurement.getMeasurements().put(userMeasurement.getNumberOfSamples(), requestDTO);
			userMeasurement.setNumberOfSamples(userMeasurement.getNumberOfSamples()+1);
		}else {
			//bisogna sovrascrivere l'ultima misura
			userMeasurement.getMeasurements().put(
					userMeasurement.getNumberOfSamples()-1,
					requestDTO);
		}
		
		userMeasurement.setComplete(carbonFootPrintsUtils.isLastMeasurementComplete(requestDTO));
		
		
		
		return userMeasurement;
	}
	
	
}
