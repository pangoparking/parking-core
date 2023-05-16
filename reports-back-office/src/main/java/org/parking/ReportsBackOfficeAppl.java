package org.parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.parking")
public class ReportsBackOfficeAppl {

	public static void main(String[] args) {
		SpringApplication.run(ReportsBackOfficeAppl.class, args);
	}

}