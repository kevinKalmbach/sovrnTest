package com.example.domain;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(toBuilder=true)
@JsonInclude(Include.NON_EMPTY)
@Immutable
public class HistoryEntry {
	private String tid;
	private int userid;
	@Singular
	private List<Bid> bids;
	private BigDecimal winningPrice;
	private int winningProvider;
	private String clickResult;
	@JsonIgnore
	private long startTime;

}
