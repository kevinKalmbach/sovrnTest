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

import com.example.domain.PartnerRequest;
import com.example.domain.PartnerResponse;

// this is just for testing (so I can actually call a simulated partner)
@RestController
@Validated
public class GarbageController {
	private Random generator = new Random();

	private static final Logger LOGGER = LoggerFactory.getLogger(GarbageController.class);

	@PostMapping("/partner/{id}")
	public PartnerResponse getPartner(@PathVariable("id") int id, @RequestBody PartnerRequest request) {
		LOGGER.info("getting partner response for {}", request);
		double b = generator.nextDouble();  // between 0-1, but that's ok
		BigDecimal bid = BigDecimal.valueOf(b);
		return PartnerResponse.builder().adhtml("H" + id).bidprice(bid).build();
	}
}
