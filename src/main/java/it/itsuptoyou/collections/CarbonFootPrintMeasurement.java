package it.itsuptoyou.collections;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import it.itsuptoyou.collections.DTO.AverageValue;
import it.itsuptoyou.collections.DTO.CarbonFootPrintDTO;
import it.itsuptoyou.utils.EntityAbstract;
import lombok.Data;

@Document(collection = "CarbonFootprintMeasurement")
@Data
public class CarbonFootPrintMeasurement extends EntityAbstract{

	private String username;
	private String type = "basic carbon footprint";
	private int numberOfSamples = 0;
	// keep track of average of each subset
	private List<AverageValue> measurementAverages;
	private List<Double> totalAverageValues;
	private LocalDateTime lastMeasurement;
	// true only if each measurement is complete
	private boolean isComplete = false;
	private Map<Integer,CarbonFootPrintDTO> measurements;
	
	
}
