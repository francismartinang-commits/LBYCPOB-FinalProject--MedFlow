package com.dlsu.medflow;

import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MedFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedFlowApplication.class, args);
	}

	// UNDERSTAND:
	// @Bean lets Spring Boot manage HospitalDataStore so it can be
	// injected into controllers such as AdminController.
	@Bean
	public HospitalDataStore hospitalDataStore() {
		return HospitalDataStore.loadOrCreate();
	}
}