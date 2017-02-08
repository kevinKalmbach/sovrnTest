package com.example.dal;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.example.domain.Provider;
import com.example.domain.ProviderRequest;
import com.example.domain.ProviderResponse;

import rx.Observable;

@Repository
public class CallToProviderImpl implements CallToProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(CallToProviderImpl.class);
	private RestTemplate restTemplate;
	private Validator validator;


	public CallToProviderImpl(RestTemplate rt, Validator validator) {
		this.restTemplate = rt;
		this.validator = validator;
	}

	private ProviderResponse getResponse(String url, ProviderRequest request ) throws Exception {
		LOGGER.info("calling uri {}: with {}", url, request);
			LOGGER.info("going to call {}", url);
			URI uri = new URI(url);
			RequestEntity<ProviderRequest> re = RequestEntity.post(uri).body(request);
			ResponseEntity<ProviderResponse> response = restTemplate.exchange(url, HttpMethod.POST, re, ProviderResponse.class);
			if (response.getStatusCode() !=  HttpStatus.OK) {
				LOGGER.info("we didn't get a 200 {}", response.getStatusCode());
				throw new Exception("didn't get a good response");
			}
			ProviderResponse pr = response.getBody();
			 Set<ConstraintViolation<ProviderResponse>> violations = validator.validate(pr);
			 if (!violations.isEmpty())
			 {
				 LOGGER.info("constraint violations");
				 throw new ConstraintViolationException(violations);
			 }
			return response.getBody();
	}
	
	private Observable<ProviderResponse> getObservable(Provider p, ProviderRequest request) {
		try {
			ProviderResponse response = getResponse(p.getUrl(),  request );
			response=response.toBuilder().providerId(p.getProviderId()).build();
			return Observable.just(response);
		}catch (Exception e) {
			LOGGER.info("returning an error ", e);
			return Observable.error(e);
		}
	}

	@Override
	public Observable<ProviderResponse> callProvider(Provider p, ProviderRequest request) {
		return Observable.defer(() -> getObservable(p,request).timeout(200, TimeUnit.MILLISECONDS))
				.onErrorResumeNext(t -> {LOGGER.info("error calling a provider, ignoring it", t); return Observable.just(null);});
	}

}
