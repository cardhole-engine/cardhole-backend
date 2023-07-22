package com.github.cardhole;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CardholeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardholeBackendApplication.class, args);
	}
}
