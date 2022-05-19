package it.itsuptoyou.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.itsuptoyou.collections.CarbonFootPrintMeasurement;

public interface CarbonFootprintRepository extends MongoRepository<CarbonFootPrintMeasurement, String>{

	Optional<CarbonFootPrintMeasurement> findByUsername(String username);
}
