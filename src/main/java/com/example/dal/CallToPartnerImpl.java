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

import com.example.domain.Partner;
import com.example.domain.PartnerRequest;
import com.example.domain.PartnerResponse;

import rx.Observable;

@Repository
public class CallToPartnerImpl implements CallToPartner {
	private static final Logger LOGGER = LoggerFactory.getLogger(CallToPartnerImpl.class);
	private RestTemplate restTemplate;
	private Validator validator;


	public CallToPartnerImpl(RestTemplate rt, Validator validator) {
		this.restTemplate = rt;
		this.validator = validator;
	}

	private PartnerResponse getResponse(String url, PartnerRequest request ) throws Exception {
		LOGGER.info("calling uri {}: with {}", url, request);
			LOGGER.info("going to call {}", url);
			URI uri = new URI(url);
			RequestEntity<PartnerRequest> re = RequestEntity.post(uri).body(request);
			ResponseEntity<PartnerResponse> response = restTemplate.exchange(url, HttpMethod.POST, re, PartnerResponse.class);
			if (response.getStatusCode() !=  HttpStatus.OK) {
				LOGGER.info("we didn't get a 200 {}", response.getStatusCode());
				throw new Exception("didn't get a good response");
			}
			PartnerResponse pr = response.getBody();
			 Set<ConstraintViolation<PartnerResponse>> violations = validator.validate(pr);
			 if (!violations.isEmpty())
			 {
				 LOGGER.info("constraint violations");
				 throw new ConstraintViolationException(violations);
			 }
			return response.getBody();
	}
	
	private Observable<PartnerResponse> getObservable(Partner p, PartnerRequest request) {
		try {
			PartnerResponse response = getResponse(p.getUrl(),  request );
			response=response.toBuilder().partnerId(p.getPartnerId()).build();
			return Observable.just(response);
		}catch (Exception e) {
			LOGGER.info("returning an error ", e);
			return Observable.error(e);
		}
	}

	@Override
	public Observable<PartnerResponse> callpartner(Partner p, PartnerRequest request) {
		return Observable.defer(() -> getObservable(p,request).timeout(200, TimeUnit.MILLISECONDS))
				.onErrorResumeNext(t -> {LOGGER.info("error calling a provider, ignoring it", t); return Observable.empty();});
	}

}
