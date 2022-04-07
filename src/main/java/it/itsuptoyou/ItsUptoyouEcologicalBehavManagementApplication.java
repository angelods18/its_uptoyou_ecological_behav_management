package it.itsuptoyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ItsUptoyouEcologicalBehavManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItsUptoyouEcologicalBehavManagementApplication.class, args);
	}

}
