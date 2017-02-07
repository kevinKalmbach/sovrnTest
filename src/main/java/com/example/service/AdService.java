package com.example.service;

import com.example.domain.AdRequest;
import com.example.domain.AdResponse;

import rx.Observable;

public interface AdService {
	Observable<AdResponse> getAd(AdRequest request);

}
