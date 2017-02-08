package com.example.dal;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.constraint.ValidationTestUtils;
import com.example.domain.Provider;
import com.example.domain.ProviderResponse;

import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class CallToProviderImplTest {
	private static final String URL = "http://test1.com";

	@Mock
	RestTemplate restTemplate;

	@Mock
	Validator validator = ValidationTestUtils.getValidator();

	@InjectMocks
	CallToProviderImpl callToProvider;

	private ProviderResponse goodProviderResponse() {
		return ProviderResponse.builder().bidprice(new BigDecimal("1.0")).adhtml("html").build();
	}

	private ResponseEntity<ProviderResponse> goodResponse() {
		ResponseEntity<ProviderResponse> response = new ResponseEntity<ProviderResponse>(goodProviderResponse(),
				HttpStatus.OK);
		return response;
	}
	private ResponseEntity<ProviderResponse> badResponse() {
		ResponseEntity<ProviderResponse> response = new ResponseEntity<ProviderResponse>(goodProviderResponse(),
				HttpStatus.INTERNAL_SERVER_ERROR);
		return response;
	}

	private Set<ConstraintViolation<Object>> getConstraintViolation() {
		@SuppressWarnings("unchecked")
		ConstraintViolation<Object> violation=Mockito.mock(ConstraintViolation.class);
		Set<ConstraintViolation<Object>> ret = new HashSet<>();
		ret.add(violation);
		return ret;
	}

	@Test
	public void goodResult() {
		Mockito.when(restTemplate.exchange(Mockito.eq(URL), Mockito.eq(HttpMethod.POST),
				Mockito.any(RequestEntity.class), Mockito.eq(ProviderResponse.class))).thenReturn(goodResponse());
		Mockito.when(validator.validate(Mockito.any())).thenReturn(Collections.emptySet());
		Provider p = Provider.builder().url(URL).build();
		Observable<ProviderResponse> x = callToProvider.callProvider(p, null);
		ProviderResponse pr = x.toBlocking().first();
		assertEquals(goodProviderResponse(), pr);
	}

	@Test
	public void constraintViolation() {
		Mockito.when(restTemplate.exchange(Mockito.eq(URL), Mockito.eq(HttpMethod.POST),
				Mockito.any(RequestEntity.class), Mockito.eq(ProviderResponse.class))).thenReturn(goodResponse());
		Mockito.when(validator.validate(Mockito.any())).thenReturn(getConstraintViolation());
		Provider p = Provider.builder().url(URL).build();
		Observable<ProviderResponse> x = callToProvider.callProvider(p, null);
		//Since we hit a constraint violation, this better return null
		ProviderResponse pr = x.toBlocking().first();
		assertEquals(null,pr);
	}
	

	@Test
	public void invalidResponse() {
		Mockito.when(restTemplate.exchange(Mockito.eq(URL), Mockito.eq(HttpMethod.POST),
				Mockito.any(RequestEntity.class), Mockito.eq(ProviderResponse.class))).thenReturn(badResponse());
		Mockito.when(validator.validate(Mockito.any())).thenReturn(Collections.emptySet());
		Provider p = Provider.builder().url(URL).build();
		Observable<ProviderResponse> x = callToProvider.callProvider(p, null);
		//Since we hit a constraint violation, this better return null
		ProviderResponse pr = x.toBlocking().first();
		assertEquals(null,pr);
	}

}
