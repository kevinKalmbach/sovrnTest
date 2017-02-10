package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.dal.ProviderDal;
import com.example.domain.AdRequest;
import com.example.domain.Provider;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixObservableCommand;

import rx.Observable;

public class GetProviderFromDBCmd extends HystrixObservableCommand<Provider> {
	  private static final Logger LOGGER = LoggerFactory.getLogger(GetProviderFromDBCmd.class);

	  private static final Setter SETTER = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProviderCommands"))
	      .andCommandKey(HystrixCommandKey.Factory.asKey("GetProviderFromDB"));

	    private final ProviderDal dal;
	    private final AdRequest request;

	    public GetProviderFromDBCmd(ProviderDal dal, AdRequest request) {
	        super(SETTER);
	        this.dal=dal;
	        this.request=request;
	       
	    }

	    @Override
	    protected Observable<Provider> construct() {
	      return dal.getProviders(request)
	          .doOnError(i -> LOGGER.info("Error in db: {}", i.toString()));
	    }

	    @Override
	    protected Observable<Provider> resumeWithFallback() {
	      LOGGER.info("In resumeWithFallback");
	      return new HardcodedProviderCommand(request).toObservable();
	    }
	}

