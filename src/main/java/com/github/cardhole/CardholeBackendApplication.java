package com.github.cardhole;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.github.cardhole")
public class CardholeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardholeBackendApplication.class, args);
	}
}
