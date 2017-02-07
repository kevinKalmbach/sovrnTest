package com.example.controller;

import java.math.BigDecimal;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.ProviderRequest;
import com.example.domain.ProviderResponse;

// this is just for testing (so I can actually call a simulated provider)
@RestController
@Validated
public class GarbageController {
	private Random generator = new Random();

	private static final Logger LOGGER = LoggerFactory.getLogger(GarbageController.class);

	@PostMapping("/provider/{id}")
	public ProviderResponse getprovider(@PathVariable("id") int id, @RequestBody ProviderRequest request) {
		LOGGER.info("getting provider response for {}", request);
		double b = generator.nextDouble();  // between 0-1, but that's ok
		BigDecimal bid = BigDecimal.valueOf(b);
		return ProviderResponse.builder().adhtml("H" + id).bidprice(bid).build();
	}
}
