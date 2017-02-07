package com.example.domain;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(buildMethodName = "nonValidatedBuild")
@JsonInclude(Include.NON_EMPTY)
@JsonDeserialize(builder = PartnerRequest.PartnerRequestBuilder.class)
@Immutable
public class PartnerRequest {
	private int width;
	private int height;
	private String domain;
	private String userip;
	private String useragent;


	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class PartnerRequestBuilder extends GenericBuilder<PartnerRequest> {
	}
}
