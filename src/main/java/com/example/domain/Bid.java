package com.example.domain;

import java.math.BigDecimal;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(Include.NON_EMPTY)
@Immutable
public class Bid {
  private int providerId;
  private BigDecimal bidPrice;
}
