package com.example.dal;

import com.example.domain.AdRequest;
import com.example.domain.Provider;

import rx.Observable;

public interface ProviderDal {
	//Given a request, return (0..n) providers
	  Observable<Provider> getProviders(AdRequest request);

}
