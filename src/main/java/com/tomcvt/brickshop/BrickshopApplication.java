package com.tomcvt.brickshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BrickshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrickshopApplication.class, args);
	}
}
