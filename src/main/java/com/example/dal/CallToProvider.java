package com.example.dal;

import com.example.domain.Provider;
import com.example.domain.ProviderRequest;
import com.example.domain.ProviderResponse;

import rx.Observable;

public interface CallToProvider {
	Observable<ProviderResponse> callProvider(Provider p, ProviderRequest request);
}
