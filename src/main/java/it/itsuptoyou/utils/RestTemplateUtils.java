package it.itsuptoyou.utils;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class RestTemplateUtils {

	
	
	public ResponseEntity<?> getRequest(String url, String token, Map<String,Object> requestParams){
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+token);
		HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
		
		ResponseEntity<?> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class, requestParams);
		}catch(Exception e) {
			log.error(e);
			return null;
		}
		log.info("status: ", response.getStatusCode());
		log.info("body: ", response.getBody());
		return ResponseEntity.ok(response.getBody());
	}
	
	public ResponseEntity<?> postRequest(String url, String token, Map<String,Object> requestParams, Map<String,Object> requestBody){
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+token);
		HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody,headers);
		
		ResponseEntity<?> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class, requestParams);
		}catch(Exception e) {
			log.error(e);
			return null;
		}
		log.info("status: ", response.getStatusCode());
		log.info("body: ", response.getBody());
		return ResponseEntity.ok(response.getBody());
	}
	
	public ResponseEntity<?> putRequest(String url, String token, Map<String,Object> requestParams, Map<String,Object> requestBody){
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+token);
		HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody,headers);
		
		ResponseEntity<?> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Map.class, requestParams);
		}catch(Exception e) {
			log.error(e);
			return null;
		}
		log.info("status: ", response.getStatusCode());
		log.info("body: ", response.getBody());
		return ResponseEntity.ok(response.getBody());
	}
	
	public ResponseEntity<?> patchRequest(String url, String token, Map<String,Object> requestParams, Map<String,Object> requestBody){
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+token);
		HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody,headers);
		
		ResponseEntity<?> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.PATCH, httpEntity, Map.class, requestParams);
		}catch(Exception e) {
			log.error(e);
			return null;
		}
		log.info("status: ", response.getStatusCode());
		log.info("body: ", response.getBody());
		return ResponseEntity.ok(response.getBody());
	}
	
	public ResponseEntity<?> deleteRequest(String url, String token, Map<String,Object> requestParams, Map<String,Object> requestBody){
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+token);
		HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody,headers);
		
		ResponseEntity<?> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Map.class, requestParams);
		}catch(Exception e) {
			log.error(e);
			return null;
		}
		log.info("status: ", response.getStatusCode());
		log.info("body: ", response.getBody());
		return ResponseEntity.ok(response.getBody());
	}
	
}
