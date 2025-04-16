package com.afterschoolclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(PolicyProperties.class)
@SpringBootApplication
public class AfterSchoolClubApplication {

	public static void main(String[] args) {
		SpringApplication.run(AfterSchoolClubApplication.class, args);
	}

}
