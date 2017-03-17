package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.domain.AdRequest;
import com.example.domain.Provider;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixObservableCommand;

import rx.Observable;

public class HardcodedProviderCmdObs extends HystrixObservableCommand<Provider> {
	private static final Provider PROVIDER = Provider.builder().providerId(4).url("http://localhost:8080/provider/4").build();
	  private static final Logger LOGGER = LoggerFactory.getLogger(HardcodedProviderCmdObs.class);

	  private static final Setter SETTER = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProviderCommands"))
	      .andCommandKey(HystrixCommandKey.Factory.asKey("getHardcodedProvider"));

	    public HardcodedProviderCmdObs(AdRequest request) {
	        super(SETTER);	       
	    }

	    @Override
	    // TODO: What are the business requirements if the primary db goes down??
	    protected Observable<Provider> construct() {
	    	LOGGER.info("getting hardcoded provider");
	    	return Observable.just(PROVIDER);
	    }
	}

