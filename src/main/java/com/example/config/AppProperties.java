package com.example.config;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class AppProperties {

	@Value("${CLICKTIMER:10}")
  private int clickTimer = 10;


}
