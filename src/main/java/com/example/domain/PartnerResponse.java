package com.example.domain;

import java.math.BigDecimal;

import javax.annotation.concurrent.Immutable;

import org.hibernate.validator.constraints.SafeHtml;

import com.example.constraint.BidPriceConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(buildMethodName = "nonValidatedBuild", toBuilder=true)
@JsonInclude(Include.NON_EMPTY)
@JsonDeserialize(builder = PartnerResponse.PartnerResponseBuilder.class)
@Immutable
public class PartnerResponse {
	private int partnerId;
	@BidPriceConstraint
	private BigDecimal bidprice;
	@SafeHtml
	private String adhtml;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class PartnerResponseBuilder extends GenericBuilder<PartnerResponse> {
	}
}
