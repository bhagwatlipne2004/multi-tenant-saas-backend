package com.bhagwat.saas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SaasBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaasBackendApplication.class, args);
	}

}
