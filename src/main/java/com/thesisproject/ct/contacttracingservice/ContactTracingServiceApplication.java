package com.thesisproject.ct.contacttracingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ContactTracingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContactTracingServiceApplication.class, args);
	}
}
