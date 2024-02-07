package com.flipkart.electronics_shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ElectronicsShoppingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicsShoppingApplication.class, args);

	}

}
