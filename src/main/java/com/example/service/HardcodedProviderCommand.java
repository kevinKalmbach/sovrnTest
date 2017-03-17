package com.example.service;

import com.example.domain.AdRequest;
import com.example.domain.Provider;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HardcodedProviderCommand extends HystrixCommand<List<Provider>> {
	private static final Provider PROVIDER = Provider.builder().providerId(4).url("http://localhost:8080/provider/4").build();
	private static final List<Provider> defaultProvider;
	  private static final Logger LOGGER = LoggerFactory.getLogger(HardcodedProviderCommand.class);

	  private static final Setter SETTER = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProviderCommands"))
	      .andCommandKey(HystrixCommandKey.Factory.asKey("getHardcodedProvider"));

	  static {
	  	 List<Provider> def  = new ArrayList<>();
	  	 def.add(PROVIDER);
		  defaultProvider = Collections.unmodifiableList(def);
	  }
	    public HardcodedProviderCommand(AdRequest request) {
	        super(SETTER);	       
	    }

	    @Override
	    // TODO: What are the business requirements if the primary db goes down??
	    protected List<Provider> run() {
	    	LOGGER.info("getting hardcoded provider");
	    	return defaultProvider;
	    }
	}

