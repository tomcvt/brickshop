package com.tomcvt.brickshop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BrickshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrickshopApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner init(Environment env) {
		return args -> {
			
		};
	}
}
