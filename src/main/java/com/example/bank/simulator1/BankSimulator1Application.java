package com.example.bank.simulator1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.bank.simulator1.config.BankProperties;
import com.example.bank.simulator1.config.BilldeskProperties;
import com.example.bank.simulator1.config.SimulatorProperties;


@SpringBootApplication
@EnableConfigurationProperties({BankProperties.class, BilldeskProperties.class, SimulatorProperties.class})
public class BankSimulator1Application {

	public static void main(String[] args) {
		SpringApplication.run(BankSimulator1Application.class, args);
		
	}

}
