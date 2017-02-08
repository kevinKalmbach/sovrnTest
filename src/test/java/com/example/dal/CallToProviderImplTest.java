package com.example.dal;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.example.constraint.ValidationTestUtils;
import com.example.domain.Provider;
import com.example.domain.ProviderResponse;

import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class CallToProviderImplTest {
	private static final String URL = "http://test1.com/whatever";
	private static final String BAD_URL = "http://test1.com/whatever2";

	@Mock
	Validator validator = ValidationTestUtils.getValidator();

	CallToProviderImpl callToProvider;

	private RestTemplate setupRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

		mockServer.expect(requestTo(URL))
		  .andRespond(withSuccess("{ \"bidprice\" : 1.0, \"adhtml\" : \"html\" }", org.springframework.http.MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo(BAD_URL))
		  .andRespond(withBadRequest());
		return restTemplate;
	}
	
	@Before
	public void setup() {
		callToProvider = new CallToProviderImpl(setupRestTemplate(), validator);
	}
	
	private ProviderResponse goodProviderResponse() {
		return ProviderResponse.builder().bidprice(new BigDecimal("1.0")).adhtml("html").build();
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
//		Mockito.when(restTemplate.exchange(Mockito.eq(URL), Mockito.eq(HttpMethod.POST),
//				Mockito.any(RequestEntity.class), Mockito.eq(ProviderResponse.class))).thenReturn(goodResponse());
		Mockito.when(validator.validate(Mockito.any())).thenReturn(Collections.emptySet());
		Provider p = Provider.builder().url(URL).build();
		Observable<ProviderResponse> x = callToProvider.callProvider(p, null);
		ProviderResponse pr = x.toBlocking().first();
		assertEquals(goodProviderResponse(), pr);
	}

	@Test
	public void constraintViolation() {
		Mockito.when(validator.validate(Mockito.any())).thenReturn(getConstraintViolation());
		Provider p = Provider.builder().url(URL).build();
		Observable<ProviderResponse> x = callToProvider.callProvider(p, null);
		//Since we hit a constraint violation, this better return null
		ProviderResponse pr = x.toBlocking().first();
		assertEquals(null,pr);
	}
	

	@Test
	public void invalidResponse() {
		
		Mockito.when(validator.validate(Mockito.any())).thenReturn(Collections.emptySet());
		Provider p = Provider.builder().url(BAD_URL).build();
		Observable<ProviderResponse> x = callToProvider.callProvider(p, null);
		//Since we hit a constraint violation, this better return null
		ProviderResponse pr = x.toBlocking().first();
		assertEquals(null,pr);
	}

}
