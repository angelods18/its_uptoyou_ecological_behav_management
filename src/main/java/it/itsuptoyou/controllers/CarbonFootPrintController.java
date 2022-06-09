package it.itsuptoyou.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.itsuptoyou.collections.CarbonFootPrintMeasurement;
import it.itsuptoyou.collections.DTO.CarbonFootPrintDTO;
import it.itsuptoyou.exceptions.PreconditionFailedException;
import it.itsuptoyou.service.CarbonFootPrintMeasureService;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class CarbonFootPrintController {

	
	private static String USERNAME = "username";
	
	@Autowired
	private CarbonFootPrintMeasureService carbonFootPrintService;
	
	@GetMapping("/public/ping")
	public String ping() {
		return "PONG";
	}
	
	/**
	 * Endpoint to register a new record of carbonFootPrintMeasurement
	 * @param request
	 * @param requestBody
	 * @return
	 * @throws PreconditionFailedException 
	 */
	@PostMapping("protected/save-carbon-footprint")
	public CarbonFootPrintMeasurement registerCarbonFootPrint(HttpServletRequest request, @RequestBody CarbonFootPrintDTO requestBody) throws PreconditionFailedException{
		String username = request.getHeader(USERNAME);
		
		return carbonFootPrintService.registerMeasurement(username, requestBody);
	}
}
