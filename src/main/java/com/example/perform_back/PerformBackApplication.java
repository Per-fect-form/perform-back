package com.example.perform_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
public class PerformBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerformBackApplication.class, args);
	}

}
