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
@JsonDeserialize(builder = AdResponse.AdResponseBuilder.class)
@Immutable
public class AdResponse {
	private String html;
	private final String tid;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class AdResponseBuilder extends GenericBuilder<AdResponse> {
	}
}
