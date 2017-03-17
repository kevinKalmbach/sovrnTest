package com.example.service;

import com.example.dal.ProviderDal;
import com.example.domain.AdRequest;
import com.example.domain.Provider;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetProviderFromDBCmd extends HystrixCommand<List<Provider>> {
	  private static final Logger LOGGER = LoggerFactory.getLogger(GetProviderFromDBCmd.class);

	  private static final HystrixCommand.Setter SETTER = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProviderCommands"))
	      .andCommandKey(HystrixCommandKey.Factory.asKey("GetProviderFromDB"));

	    private final ProviderDal dal;
	    private final AdRequest request;

	    public GetProviderFromDBCmd(ProviderDal dal, AdRequest request) {
	        super(SETTER);
	        this.dal=dal;
	        this.request=request;
	       
	    }

	    @Override
	    protected List<Provider> run() {
	      return dal.getProviders(request);
	    }

	    @Override
	    protected List<Provider> getFallback() {
	      LOGGER.info("In fallback");
	      return new HardcodedProviderCommand(request).execute();
	    }
	}

