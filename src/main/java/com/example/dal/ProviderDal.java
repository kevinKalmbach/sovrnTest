package com.example.dal;

import com.example.domain.AdRequest;
import com.example.domain.Provider;

import rx.Observable;

import java.util.List;

public interface ProviderDal {
	//Given a request, return (0..n) providers
	  Observable<Provider> getProvidersObs(AdRequest request);
	List<Provider> getProviders(AdRequest request);


}
