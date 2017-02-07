package com.example.domain;

import java.util.UUID;

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
@JsonDeserialize(builder = AdRequest.AdRequestBuilder.class)
@Immutable
public class AdRequest {
	private int width;
	private int height;
	private String domain;
	private int userid;
	private String userip;
	private String userAgent;
	private final String tid = UUID.randomUUID().toString();

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class AdRequestBuilder extends GenericBuilder<AdRequest> {
	}
}
