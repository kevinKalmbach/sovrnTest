package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {


	@Bean
	 public RestTemplate restTemplate() {
		    return new RestTemplate();
    }
	
	@Bean
	public AppProperties appProperties() {
		return new AppProperties();
	}
	@Bean
	public DBProperties dbProperties() {
		return new DBProperties();
	}

}
