package it.itsuptoyou.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class CarbonFootPrintController {

	@GetMapping("/public/ping")
	public String ping() {
		return "PONG";
	}
}
