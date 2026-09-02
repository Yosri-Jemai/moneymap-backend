package com.yosri.moneymap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneymapApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneymapApplication.class, args);
	}

}
