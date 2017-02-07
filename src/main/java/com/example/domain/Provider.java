package com.example.domain;

import javax.annotation.concurrent.Immutable;

import org.hibernate.validator.constraints.URL;

import com.example.constraint.ProviderIdConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(buildMethodName = "nonValidatedBuild")
@JsonInclude(Include.NON_EMPTY)
@JsonDeserialize(builder = Provider.ProviderBuilder.class)
@Immutable
public class Provider {
	@ProviderIdConstraint
	private int providerId;
	@URL
    private String url;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class ProviderBuilder extends GenericBuilder<Provider> {
	}
}
