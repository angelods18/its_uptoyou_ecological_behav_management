package it.itsuptoyou.controllers;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	public ResponseEntity<CarbonFootPrintMeasurement> registerCarbonFootPrint(HttpServletRequest request, @RequestBody CarbonFootPrintDTO requestBody) throws PreconditionFailedException{
		String username = request.getHeader(USERNAME);
		
		return ResponseEntity.ok(carbonFootPrintService.registerMeasurement(username, requestBody));
	}
	
	/**
	 * Get measurment of the user that does the request
	 * @param request
	 * @return
	 */
	@GetMapping("protected/get-carbon-footprint")
	public ResponseEntity<CarbonFootPrintMeasurement> getCarbonFootPrint(HttpServletRequest request) {
		String username = request.getHeader("username");
		return ResponseEntity.ok(carbonFootPrintService.getUserMeasurement(username));
	}
	
	/**
	 * Get measurement of the user with {username}
	 * @param request
	 * @param username
	 * @return
	 */
	@GetMapping("protected/get-carbon-footprint/{username}")
	public ResponseEntity<CarbonFootPrintMeasurement> getUserCarbonFootPrint(HttpServletRequest request, @PathVariable("username") String username){
		
		return ResponseEntity.ok(carbonFootPrintService.getUserMeasurement(username));
	}
	
	@PostMapping("protected/carbon-footprint/comment")
	public ResponseEntity<?> addCommentToMeasurement(HttpServletRequest request, @RequestBody Map<String,Object> requestBody){

		String token = request.getHeader("token");
		String username = request.getHeader("username");
		requestBody.put("token", token);
		carbonFootPrintService.saveCommentToMeasurement(username, requestBody);
		return ResponseEntity.ok(null);
	}
	
}
