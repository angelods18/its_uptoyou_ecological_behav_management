package it.itsuptoyou.utils;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateUtils {

	private RestTemplate restTemplate;
	
	public RestTemplateUtils() {
		this.restTemplate=new RestTemplate();
	}
	
	public ResponseEntity<?> getRequest(String url, String token, Map<String,Object> requestBody){
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+token);
		return ResponseEntity.ok(null);
	}
}
