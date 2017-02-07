package com.example.dal;

import com.example.domain.AdRequest;
import com.example.domain.Partner;

import rx.Observable;

public interface PartnerDal {
	//Given a request, return (0..n) partners
	  Observable<Partner> getPartners(AdRequest request);

}
